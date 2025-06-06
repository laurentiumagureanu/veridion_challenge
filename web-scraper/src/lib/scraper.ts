import * as cheerio from 'cheerio';

const SOCIAL_MEDIA_DOMAINS: string[] = [
  'facebook.com',
  'instagram.com',
  'linkedin.com',
  'twitter.com',
  'youtube.com',
  'tiktok.com',
  'x.com',
];

const URL_REGEX = /((https?:)?\/\/)?(www\.)?[-a-zA-Z0-9@:%._\+~#=]{1,256}\.[a-zA-Z\.]{1,6}\b([-a-zA-Z0-9()@:%_\+.~#?&//=]*)/g;
const PHONE_NUMBER_REGEX = /(\+\d{1,2}[-.\s]?)?\(?\d{3,4}\)?[-.\s]?\d{3}[-.\s]?\d{3,4}/g;
const ADDRESS_REGEX = /\d+(\s[A-Za-z]){0,2}\s[A-Za-z]+\s(?:Street|St|Avenue|Ave|Boulevard|Blvd|Road|Rd|Lane|Ln|Drive|Dr|Court|Ct|Square|Sq|Loop|Lp|Parkway|Pkwy|Trail|Trl|Terrace|Ter|Place|Pl|Way|Wy)\,?\s[A-Za-z]+\,?\s[A-Z]{2}\s\d{5}/g;

interface FetchSiteResult {
  url: string;
  text: string;
}

interface ExtractedInfo {
  phoneNumbers: string[];
  socialMediaLinks: string[];
  address: string | null;
}

interface ScrapeResult {
  domain: string;
  website?: string;
  status: 'COMPLETED' | 'FAILED';
  phoneNumbers?: string[];
  socialMediaLinks?: string[];
  address?: string | null;
}

const fetchSite = async (url: string): Promise<FetchSiteResult> => {
  const response = await fetch(url, {
    headers: {
      'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36'
    }
  });
  
  return {
    url: response.url,
    text: await response.text()
  };
};

const loadHtml = (html: string): cheerio.CheerioAPI => {
  const $ = cheerio.load(html);
  $('script').remove();
  $('style').remove();
  $('link').remove();
  $('img').remove();
  $('iframe').remove();
  $('video').remove();
  $('audio').remove();
  $('object').remove();
  $('textarea').remove();
  $('svg').remove();
  $('canvas').remove();

  return $;
};

const extractInfoFromHtml = ($: cheerio.CheerioAPI): ExtractedInfo => {
  const phoneNumbers: string[] = [];
  const socialMediaLinks: string[] = [];
  let address: string | null = null;

  $('a').map((_i: number, el: any) => $(el).attr('href')).get()
    .filter((link: string) => SOCIAL_MEDIA_DOMAINS.some(domain => link.toLowerCase().includes(domain)))
    .forEach((link: string) => socialMediaLinks.push(link));
    
  const randomNumbers = new Set<string>();

  ($('body').text().match(URL_REGEX) || [])
    .forEach((url: string) => {
      (url.match(PHONE_NUMBER_REGEX) || []).forEach((phoneNumber: string) => randomNumbers.add(phoneNumber));
    });

  ($('body').text().match(PHONE_NUMBER_REGEX) || [])
    .forEach((phoneNumber: string) => !randomNumbers.has(phoneNumber) && phoneNumbers.push(phoneNumber));

  const addressMatches = new Set($('body').text().match(ADDRESS_REGEX));
  if (addressMatches.size > 0) {
    address = Array.from(addressMatches).join(', ');
  }

  return {
    phoneNumbers,
    socialMediaLinks, 
    address
  };
};

export const extract = async (domain: string): Promise<ScrapeResult> => {
  try {
    const { text, url } = await fetchSite(`http://${domain}`);
    const $ = loadHtml(text);

    let { phoneNumbers: basePhoneNumbers, socialMediaLinks: baseSocialMediaLinks, address: baseAddress } = extractInfoFromHtml($);
    let phoneNumbers: string[];
    let socialMediaLinks: string[];
    let address: string | null;

    const contactPage = $('a[href*="contact"]').attr('href');

    if (contactPage) {
      const contactUrl = contactPage.startsWith('http') 
        ? contactPage 
        : `http://${domain}${contactPage.startsWith('/') ? contactPage : '/' + contactPage}`;
      
      const { text: contactData } = await fetchSite(contactUrl);
      const $contact = loadHtml(contactData);

      const { phoneNumbers: contactPhoneNumbers, socialMediaLinks: contactSocialMediaLinks, address: contactAddress } = extractInfoFromHtml($contact);

      phoneNumbers = Array.from(new Set([...basePhoneNumbers, ...contactPhoneNumbers]));
      socialMediaLinks = Array.from(new Set([...baseSocialMediaLinks, ...contactSocialMediaLinks]));

      if (contactAddress) {
        address = contactAddress;
      } else {
        address = baseAddress;
      }
    } else {
      phoneNumbers = Array.from(new Set([...basePhoneNumbers]));
      socialMediaLinks = Array.from(new Set([...baseSocialMediaLinks]));
      address = baseAddress;
    }

    return {
      domain,
      website: url,
      status: 'COMPLETED',
      phoneNumbers, 
      socialMediaLinks, 
      address
    };
  } catch (error) {
    console.error('Scraping error:', error);
    return { 
      domain, 
      status: 'FAILED'
    }; 
  }
}; 