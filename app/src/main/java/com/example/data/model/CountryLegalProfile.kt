package com.example.data.model

data class CountryLegalProfile(
    val code: String,              // ISO 3166-1 alpha-2, e.g. "US"
    val name: String,              // e.g. "United States"
    val region: String,            // e.g. "Americas", "Europe", "Asia-Pacific", "Middle East & Africa"
    val flagEmoji: String,         // e.g. "🇺🇸"
    val currencyCode: String,      // ISO 4217, e.g. "USD"
    val currencySymbol: String,    // e.g. "$"
    val currencyName: String,      // e.g. "US Dollar"
    val jurisdictionName: String,  // e.g. "United States Federal Jurisdiction"
    val regulatoryStandard: String // e.g. "FinCEN & IRS Recordkeeping Standard"
)

object GlobalJurisdictions {
    val allCountries: List<CountryLegalProfile> = listOf(
        CountryLegalProfile(
            code = "US",
            name = "United States",
            region = "Americas",
            flagEmoji = "🇺🇸",
            currencyCode = "USD",
            currencySymbol = "$",
            currencyName = "US Dollar",
            jurisdictionName = "United States Federal Jurisdiction",
            regulatoryStandard = "FinCEN & GAAP Standard"
        ),
        CountryLegalProfile(
            code = "GB",
            name = "United Kingdom",
            region = "Europe",
            flagEmoji = "🇬🇧",
            currencyCode = "GBP",
            currencySymbol = "£",
            currencyName = "British Pound",
            jurisdictionName = "HM Treasury & UK Jurisdiction",
            regulatoryStandard = "FCA Personal Ledger Regulation"
        ),
        CountryLegalProfile(
            code = "DE",
            name = "Germany",
            region = "Europe",
            flagEmoji = "🇩🇪",
            currencyCode = "EUR",
            currencySymbol = "€",
            currencyName = "Euro",
            jurisdictionName = "Federal Republic of Germany / EU Jurisdiction",
            regulatoryStandard = "BaFin & ECB Framework"
        ),
        CountryLegalProfile(
            code = "FR",
            name = "France",
            region = "Europe",
            flagEmoji = "🇫🇷",
            currencyCode = "EUR",
            currencySymbol = "€",
            currencyName = "Euro",
            jurisdictionName = "French Republic / EU Jurisdiction",
            regulatoryStandard = "AMF & Banque de France Regulation"
        ),
        CountryLegalProfile(
            code = "CA",
            name = "Canada",
            region = "Americas",
            flagEmoji = "🇨🇦",
            currencyCode = "CAD",
            currencySymbol = "CA$",
            currencyName = "Canadian Dollar",
            jurisdictionName = "Canada Federal Financial Jurisdiction",
            regulatoryStandard = "FINTRAC Accounting Standard"
        ),
        CountryLegalProfile(
            code = "AU",
            name = "Australia",
            region = "Asia-Pacific",
            flagEmoji = "🇦🇺",
            currencyCode = "AUD",
            currencySymbol = "A$",
            currencyName = "Australian Dollar",
            jurisdictionName = "Commonwealth of Australia Jurisdiction",
            regulatoryStandard = "ASIC & ATO Compliance"
        ),
        CountryLegalProfile(
            code = "JP",
            name = "Japan",
            region = "Asia-Pacific",
            flagEmoji = "🇯🇵",
            currencyCode = "JPY",
            currencySymbol = "¥",
            currencyName = "Japanese Yen",
            jurisdictionName = "State of Japan Financial Jurisdiction",
            regulatoryStandard = "JFSA Financial Code"
        ),
        CountryLegalProfile(
            code = "CH",
            name = "Switzerland",
            region = "Europe",
            flagEmoji = "🇨🇭",
            currencyCode = "CHF",
            currencySymbol = "CHF",
            currencyName = "Swiss Franc",
            jurisdictionName = "Swiss Confederation Banking Jurisdiction",
            regulatoryStandard = "FINMA Sovereign Privacy Accord"
        ),
        CountryLegalProfile(
            code = "IN",
            name = "India",
            region = "Asia-Pacific",
            flagEmoji = "🇮🇳",
            currencyCode = "INR",
            currencySymbol = "₹",
            currencyName = "Indian Rupee",
            jurisdictionName = "Republic of India Sovereign Jurisdiction",
            regulatoryStandard = "RBI Financial Ledger Framework"
        ),
        CountryLegalProfile(
            code = "SG",
            name = "Singapore",
            region = "Asia-Pacific",
            flagEmoji = "🇸🇬",
            currencyCode = "SGD",
            currencySymbol = "S$",
            currencyName = "Singapore Dollar",
            jurisdictionName = "Republic of Singapore Financial Jurisdiction",
            regulatoryStandard = "MAS Regulatory Framework"
        ),
        CountryLegalProfile(
            code = "AE",
            name = "United Arab Emirates",
            region = "Middle East & Africa",
            flagEmoji = "🇦🇪",
            currencyCode = "AED",
            currencySymbol = "AED",
            currencyName = "UAE Dirham",
            jurisdictionName = "United Arab Emirates Federal Jurisdiction",
            regulatoryStandard = "CBUAE Banking Regulatory Standard"
        ),
        CountryLegalProfile(
            code = "SA",
            name = "Saudi Arabia",
            region = "Middle East & Africa",
            flagEmoji = "🇸🇦",
            currencyCode = "SAR",
            currencySymbol = "SAR",
            currencyName = "Saudi Riyal",
            jurisdictionName = "Kingdom of Saudi Arabia Financial Jurisdiction",
            regulatoryStandard = "SAMA Financial Accounting Rules"
        ),
        CountryLegalProfile(
            code = "NG",
            name = "Nigeria",
            region = "Middle East & Africa",
            flagEmoji = "🇳🇬",
            currencyCode = "NGN",
            currencySymbol = "₦",
            currencyName = "Nigerian Naira",
            jurisdictionName = "Federal Republic of Nigeria Jurisdiction",
            regulatoryStandard = "CBN Monetary Accounting Standards"
        ),
        CountryLegalProfile(
            code = "ZA",
            name = "South Africa",
            region = "Middle East & Africa",
            flagEmoji = "🇿🇦",
            currencyCode = "ZAR",
            currencySymbol = "R",
            currencyName = "South African Rand",
            jurisdictionName = "Republic of South Africa Jurisdiction",
            regulatoryStandard = "SARB Ledger Regulations"
        ),
        CountryLegalProfile(
            code = "BR",
            name = "Brazil",
            region = "Americas",
            flagEmoji = "🇧🇷",
            currencyCode = "BRL",
            currencySymbol = "R$",
            currencyName = "Brazilian Real",
            jurisdictionName = "Federative Republic of Brazil Jurisdiction",
            regulatoryStandard = "BACEN Financial Standards"
        ),
        CountryLegalProfile(
            code = "MX",
            name = "Mexico",
            region = "Americas",
            flagEmoji = "🇲🇽",
            currencyCode = "MXN",
            currencySymbol = "Mex$",
            currencyName = "Mexican Peso",
            jurisdictionName = "United Mexican States Jurisdiction",
            regulatoryStandard = "Banxico & SAT Financial Standard"
        ),
        CountryLegalProfile(
            code = "KR",
            name = "South Korea",
            region = "Asia-Pacific",
            flagEmoji = "🇰🇷",
            currencyCode = "KRW",
            currencySymbol = "₩",
            currencyName = "South Korean Won",
            jurisdictionName = "Republic of Korea Jurisdiction",
            regulatoryStandard = "FSC Regulatory Standard"
        ),
        CountryLegalProfile(
            code = "CN",
            name = "China",
            region = "Asia-Pacific",
            flagEmoji = "🇨🇳",
            currencyCode = "CNY",
            currencySymbol = "¥",
            currencyName = "Chinese Yuan",
            jurisdictionName = "People's Republic of China Jurisdiction",
            regulatoryStandard = "PBOC Ledger Guidelines"
        ),
        CountryLegalProfile(
            code = "NZ",
            name = "New Zealand",
            region = "Asia-Pacific",
            flagEmoji = "🇳🇿",
            currencyCode = "NZD",
            currencySymbol = "NZ$",
            currencyName = "New Zealand Dollar",
            jurisdictionName = "New Zealand Financial Jurisdiction",
            regulatoryStandard = "RBNZ Financial Standards"
        ),
        CountryLegalProfile(
            code = "SE",
            name = "Sweden",
            region = "Europe",
            flagEmoji = "🇸🇪",
            currencyCode = "SEK",
            currencySymbol = "kr",
            currencyName = "Swedish Krona",
            jurisdictionName = "Kingdom of Sweden Jurisdiction",
            regulatoryStandard = "Riksbank & FI Regulations"
        ),
        CountryLegalProfile(
            code = "NO",
            name = "Norway",
            region = "Europe",
            flagEmoji = "🇳🇴",
            currencyCode = "NOK",
            currencySymbol = "kr",
            currencyName = "Norwegian Krone",
            jurisdictionName = "Kingdom of Norway Jurisdiction",
            regulatoryStandard = "Norges Bank Regulation"
        ),
        CountryLegalProfile(
            code = "DK",
            name = "Denmark",
            region = "Europe",
            flagEmoji = "🇩🇰",
            currencyCode = "DKK",
            currencySymbol = "kr",
            currencyName = "Danish Krone",
            jurisdictionName = "Kingdom of Denmark Jurisdiction",
            regulatoryStandard = "Danmarks Nationalbank Standard"
        ),
        CountryLegalProfile(
            code = "PL",
            name = "Poland",
            region = "Europe",
            flagEmoji = "🇵🇱",
            currencyCode = "PLN",
            currencySymbol = "zł",
            currencyName = "Polish Zloty",
            jurisdictionName = "Republic of Poland Jurisdiction",
            regulatoryStandard = "NBP Financial Code"
        ),
        CountryLegalProfile(
            code = "IT",
            name = "Italy",
            region = "Europe",
            flagEmoji = "🇮🇹",
            currencyCode = "EUR",
            currencySymbol = "€",
            currencyName = "Euro",
            jurisdictionName = "Italian Republic / EU Jurisdiction",
            regulatoryStandard = "Banca d'Italia Framework"
        ),
        CountryLegalProfile(
            code = "ES",
            name = "Spain",
            region = "Europe",
            flagEmoji = "🇪🇸",
            currencyCode = "EUR",
            currencySymbol = "€",
            currencyName = "Euro",
            jurisdictionName = "Kingdom of Spain / EU Jurisdiction",
            regulatoryStandard = "Banco de España Framework"
        ),
        CountryLegalProfile(
            code = "NL",
            name = "Netherlands",
            region = "Europe",
            flagEmoji = "🇳🇱",
            currencyCode = "EUR",
            currencySymbol = "€",
            currencyName = "Euro",
            jurisdictionName = "Kingdom of the Netherlands / EU Jurisdiction",
            regulatoryStandard = "DNB & AFM Regulations"
        ),
        CountryLegalProfile(
            code = "IE",
            name = "Ireland",
            region = "Europe",
            flagEmoji = "🇮🇪",
            currencyCode = "EUR",
            currencySymbol = "€",
            currencyName = "Euro",
            jurisdictionName = "Republic of Ireland / EU Jurisdiction",
            regulatoryStandard = "Central Bank of Ireland Framework"
        ),
        CountryLegalProfile(
            code = "TR",
            name = "Turkey",
            region = "Europe",
            flagEmoji = "🇹🇷",
            currencyCode = "TRY",
            currencySymbol = "₺",
            currencyName = "Turkish Lira",
            jurisdictionName = "Republic of Turkey Jurisdiction",
            regulatoryStandard = "TCMB Financial Regulations"
        ),
        CountryLegalProfile(
            code = "ID",
            name = "Indonesia",
            region = "Asia-Pacific",
            flagEmoji = "🇮🇩",
            currencyCode = "IDR",
            currencySymbol = "Rp",
            currencyName = "Indonesian Rupiah",
            jurisdictionName = "Republic of Indonesia Jurisdiction",
            regulatoryStandard = "Bank Indonesia Standards"
        ),
        CountryLegalProfile(
            code = "MY",
            name = "Malaysia",
            region = "Asia-Pacific",
            flagEmoji = "🇲🇾",
            currencyCode = "MYR",
            currencySymbol = "RM",
            currencyName = "Malaysian Ringgit",
            jurisdictionName = "Federation of Malaysia Jurisdiction",
            regulatoryStandard = "Bank Negara Malaysia Standards"
        ),
        CountryLegalProfile(
            code = "PH",
            name = "Philippines",
            region = "Asia-Pacific",
            flagEmoji = "🇵🇭",
            currencyCode = "PHP",
            currencySymbol = "₱",
            currencyName = "Philippine Peso",
            jurisdictionName = "Republic of the Philippines Jurisdiction",
            regulatoryStandard = "BSP Financial Code"
        ),
        CountryLegalProfile(
            code = "TH",
            name = "Thailand",
            region = "Asia-Pacific",
            flagEmoji = "🇹🇭",
            currencyCode = "THB",
            currencySymbol = "฿",
            currencyName = "Thai Baht",
            jurisdictionName = "Kingdom of Thailand Jurisdiction",
            regulatoryStandard = "Bank of Thailand Standard"
        ),
        CountryLegalProfile(
            code = "VN",
            name = "Vietnam",
            region = "Asia-Pacific",
            flagEmoji = "🇻🇳",
            currencyCode = "VND",
            currencySymbol = "₫",
            currencyName = "Vietnamese Dong",
            jurisdictionName = "Socialist Republic of Vietnam Jurisdiction",
            regulatoryStandard = "SBV Monetary Standard"
        ),
        CountryLegalProfile(
            code = "EG",
            name = "Egypt",
            region = "Middle East & Africa",
            flagEmoji = "🇪🇬",
            currencyCode = "EGP",
            currencySymbol = "E£",
            currencyName = "Egyptian Pound",
            jurisdictionName = "Arab Republic of Egypt Jurisdiction",
            regulatoryStandard = "CBE Accounting Standard"
        ),
        CountryLegalProfile(
            code = "KE",
            name = "Kenya",
            region = "Middle East & Africa",
            flagEmoji = "🇰🇪",
            currencyCode = "KES",
            currencySymbol = "KSh",
            currencyName = "Kenyan Shilling",
            jurisdictionName = "Republic of Kenya Jurisdiction",
            regulatoryStandard = "CBK Financial Framework"
        ),
        CountryLegalProfile(
            code = "GH",
            name = "Ghana",
            region = "Middle East & Africa",
            flagEmoji = "🇬🇭",
            currencyCode = "GHS",
            currencySymbol = "GH₵",
            currencyName = "Ghanaian Cedi",
            jurisdictionName = "Republic of Ghana Jurisdiction",
            regulatoryStandard = "Bank of Ghana Standards"
        ),
        CountryLegalProfile(
            code = "AR",
            name = "Argentina",
            region = "Americas",
            flagEmoji = "🇦🇷",
            currencyCode = "ARS",
            currencySymbol = "ARS$",
            currencyName = "Argentine Peso",
            jurisdictionName = "Argentine Republic Jurisdiction",
            regulatoryStandard = "BCRA Accounting Standard"
        ),
        CountryLegalProfile(
            code = "CL",
            name = "Chile",
            region = "Americas",
            flagEmoji = "🇨🇱",
            currencyCode = "CLP",
            currencySymbol = "CLP$",
            currencyName = "Chilean Peso",
            jurisdictionName = "Republic of Chile Jurisdiction",
            regulatoryStandard = "Banco Central de Chile Standard"
        ),
        CountryLegalProfile(
            code = "CO",
            name = "Colombia",
            region = "Americas",
            flagEmoji = "🇨🇴",
            currencyCode = "COP",
            currencySymbol = "COL$",
            currencyName = "Colombian Peso",
            jurisdictionName = "Republic of Colombia Jurisdiction",
            regulatoryStandard = "BRC Financial Regulation"
        ),
        CountryLegalProfile(
            code = "IL",
            name = "Israel",
            region = "Middle East & Africa",
            flagEmoji = "🇮🇱",
            currencyCode = "ILS",
            currencySymbol = "₪",
            currencyName = "Israeli New Shekel",
            jurisdictionName = "State of Israel Financial Jurisdiction",
            regulatoryStandard = "Bank of Israel Directive"
        )
    )

    fun findByCode(code: String): CountryLegalProfile {
        return allCountries.find { it.code.equals(code, ignoreCase = true) } ?: allCountries.first()
    }
}
