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
        ),
        CountryLegalProfile(
            code = "ET",
            name = "Ethiopia",
            region = "Middle East & Africa",
            flagEmoji = "🇪🇹",
            currencyCode = "ETB",
            currencySymbol = "Br",
            currencyName = "Ethiopian Birr",
            jurisdictionName = "Federal Democratic Republic of Ethiopia",
            regulatoryStandard = "NBE Financial Framework"
        ),
        CountryLegalProfile(
            code = "RW",
            name = "Rwanda",
            region = "Middle East & Africa",
            flagEmoji = "🇷🇼",
            currencyCode = "RWF",
            currencySymbol = "FRw",
            currencyName = "Rwandan Franc",
            jurisdictionName = "Republic of Rwanda Financial Authority",
            regulatoryStandard = "BNR Monetary Directive"
        ),
        CountryLegalProfile(
            code = "TZ",
            name = "Tanzania",
            region = "Middle East & Africa",
            flagEmoji = "🇹🇿",
            currencyCode = "TZS",
            currencySymbol = "TSh",
            currencyName = "Tanzanian Shilling",
            jurisdictionName = "United Republic of Tanzania Jurisdiction",
            regulatoryStandard = "BOT Financial Accord"
        ),
        CountryLegalProfile(
            code = "UG",
            name = "Uganda",
            region = "Middle East & Africa",
            flagEmoji = "🇺🇬",
            currencyCode = "UGX",
            currencySymbol = "USh",
            currencyName = "Ugandan Shilling",
            jurisdictionName = "Republic of Uganda Jurisdiction",
            regulatoryStandard = "BOU Regulatory Standards"
        ),
        CountryLegalProfile(
            code = "PK",
            name = "Pakistan",
            region = "Asia-Pacific",
            flagEmoji = "🇵🇰",
            currencyCode = "PKR",
            currencySymbol = "Rs",
            currencyName = "Pakistani Rupee",
            jurisdictionName = "Islamic Republic of Pakistan Jurisdiction",
            regulatoryStandard = "State Bank of Pakistan Framework"
        ),
        CountryLegalProfile(
            code = "BD",
            name = "Bangladesh",
            region = "Asia-Pacific",
            flagEmoji = "🇧🇩",
            currencyCode = "BDT",
            currencySymbol = "৳",
            currencyName = "Bangladeshi Taka",
            jurisdictionName = "People's Republic of Bangladesh Jurisdiction",
            regulatoryStandard = "Bangladesh Bank Standard"
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
            regulatoryStandard = "BSP Financial Framework"
        ),
        CountryLegalProfile(
            code = "ID",
            name = "Indonesia",
            region = "Asia-Pacific",
            flagEmoji = "🇮🇩",
            currencyCode = "IDR",
            currencySymbol = "Rp",
            currencyName = "Indonesian Rupiah",
            jurisdictionName = "Republic of Indonesia Financial Jurisdiction",
            regulatoryStandard = "Bank Indonesia / OJK Standard"
        ),
        CountryLegalProfile(
            code = "MY",
            name = "Malaysia",
            region = "Asia-Pacific",
            flagEmoji = "🇲🇾",
            currencyCode = "MYR",
            currencySymbol = "RM",
            currencyName = "Malaysian Ringgit",
            jurisdictionName = "Federation of Malaysia Financial Jurisdiction",
            regulatoryStandard = "Bank Negara Malaysia Standard"
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
            regulatoryStandard = "State Bank of Vietnam Framework"
        ),
        CountryLegalProfile(
            code = "NZ",
            name = "New Zealand",
            region = "Asia-Pacific",
            flagEmoji = "🇳🇿",
            currencyCode = "NZD",
            currencySymbol = "NZ$",
            currencyName = "New Zealand Dollar",
            jurisdictionName = "Realm of New Zealand Jurisdiction",
            regulatoryStandard = "RBNZ Financial Standards"
        ),
        CountryLegalProfile(
            code = "TR",
            name = "Turkey",
            region = "Europe",
            flagEmoji = "🇹🇷",
            currencyCode = "TRY",
            currencySymbol = "₺",
            currencyName = "Turkish Lira",
            jurisdictionName = "Republic of Turkey Financial Jurisdiction",
            regulatoryStandard = "CBRT Regulatory Standard"
        ),
        CountryLegalProfile(
            code = "PL",
            name = "Poland",
            region = "Europe",
            flagEmoji = "🇵🇱",
            currencyCode = "PLN",
            currencySymbol = "zł",
            currencyName = "Polish Zloty",
            jurisdictionName = "Republic of Poland Financial Jurisdiction",
            regulatoryStandard = "NBP Financial Framework"
        ),
        CountryLegalProfile(
            code = "SE",
            name = "Sweden",
            region = "Europe",
            flagEmoji = "🇸🇪",
            currencyCode = "SEK",
            currencySymbol = "kr",
            currencyName = "Swedish Krona",
            jurisdictionName = "Kingdom of Sweden Financial Jurisdiction",
            regulatoryStandard = "Riksbank Regulatory Accord"
        ),
        CountryLegalProfile(
            code = "NO",
            name = "Norway",
            region = "Europe",
            flagEmoji = "🇳🇴",
            currencyCode = "NOK",
            currencySymbol = "kr",
            currencyName = "Norwegian Krone",
            jurisdictionName = "Kingdom of Norway Financial Jurisdiction",
            regulatoryStandard = "Norges Bank Monetary Directive"
        ),
        CountryLegalProfile(
            code = "SA",
            name = "Saudi Arabia",
            region = "Middle East & Africa",
            flagEmoji = "🇸🇦",
            currencyCode = "SAR",
            currencySymbol = "SAR",
            currencyName = "Saudi Riyal",
            jurisdictionName = "Kingdom of Saudi Arabia Jurisdiction",
            regulatoryStandard = "SAMA Financial Standards"
        ),
        CountryLegalProfile(
            code = "QA",
            name = "Qatar",
            region = "Middle East & Africa",
            flagEmoji = "🇶🇦",
            currencyCode = "QAR",
            currencySymbol = "QR",
            currencyName = "Qatari Riyal",
            jurisdictionName = "State of Qatar Financial Jurisdiction",
            regulatoryStandard = "QCB Regulatory Framework"
        ),
        CountryLegalProfile(
            code = "JM",
            name = "Jamaica",
            region = "Americas",
            flagEmoji = "🇯🇲",
            currencyCode = "JMD",
            currencySymbol = "J$",
            currencyName = "Jamaican Dollar",
            jurisdictionName = "Jamaica Sovereign Jurisdiction",
            regulatoryStandard = "Bank of Jamaica Framework"
        )
    )

    /**
     * Converts a 2-letter ISO country code into Unicode flag emoji.
     * E.g. "NG" -> 🇳🇬, "US" -> 🇺🇸, "GB" -> 🇬🇧
     */
    fun getFlagEmoji(countryCode: String): String {
        if (countryCode.length != 2) return "🌐"
        val upper = countryCode.uppercase(java.util.Locale.US)
        val firstChar = upper[0].code - 'A'.code + 0x1F1E6
        val secondChar = upper[1].code - 'A'.code + 0x1F1E6
        return String(Character.toChars(firstChar)) + String(Character.toChars(secondChar))
    }

    /**
     * Dynamically generates a CountryLegalProfile from system Locale & Currency.
     * Enables automatic coverage for all 195+ sovereign countries and 240+ ISO territories in the world.
     */
    fun createDynamicProfile(countryCode: String): CountryLegalProfile {
        val upperCode = countryCode.uppercase(java.util.Locale.US)
        val locale = java.util.Locale("", upperCode)
        val countryName = locale.displayCountry.takeIf { it.isNotBlank() } ?: upperCode
        val flag = getFlagEmoji(upperCode)

        var currencyCode = "USD"
        var currencySymbol = "$"
        var currencyName = "US Dollar"

        try {
            val curr = java.util.Currency.getInstance(locale)
            if (curr != null) {
                currencyCode = curr.currencyCode
                currencySymbol = curr.getSymbol(java.util.Locale.getDefault())
                currencyName = curr.displayName
            }
        } catch (_: Exception) {
            // fallback
        }

        val region = when (upperCode) {
            in listOf("NG", "GH", "KE", "ZA", "EG", "ET", "RW", "TZ", "UG", "MA", "DZ", "SN", "CM", "AO", "ZM", "ZW", "AE", "SA", "QA", "KW", "OM", "BH", "IL", "JO") -> "Middle East & Africa"
            in listOf("US", "CA", "MX", "BR", "AR", "CO", "CL", "PE", "JM", "CR", "PA", "DO") -> "Americas"
            in listOf("GB", "DE", "FR", "IT", "ES", "NL", "CH", "SE", "NO", "DK", "PL", "TR", "IE", "BE", "AT", "PT", "GR", "CZ", "HU", "RO", "UA") -> "Europe"
            else -> "Asia-Pacific"
        }

        return CountryLegalProfile(
            code = upperCode,
            name = countryName,
            region = region,
            flagEmoji = flag,
            currencyCode = currencyCode,
            currencySymbol = currencySymbol,
            currencyName = currencyName,
            jurisdictionName = "$countryName Sovereign Jurisdiction",
            regulatoryStandard = "$countryName Central Bank & Financial Standard"
        )
    }

    /**
     * Automatically detects the user's home country and native currency using the Android device locale.
     * Never defaults blindly to US dollars ($) if the user is in Nigeria (₦), UK (£), India (₹), Kenya (KSh), etc.!
     */
    fun detectDefaultCountry(): CountryLegalProfile {
        try {
            val deviceCountry = java.util.Locale.getDefault().country
            if (!deviceCountry.isNullOrBlank()) {
                val found = allCountries.find { it.code.equals(deviceCountry, ignoreCase = true) }
                if (found != null) {
                    return found
                }
                return createDynamicProfile(deviceCountry)
            }
        } catch (_: Exception) {
            // fallback below
        }
        return allCountries.first()
    }

    /**
     * Returns full world coverage (curated + dynamically resolved ISO countries)
     * covering all 195+ sovereign nations in the world.
     */
    val allWorldCountries: List<CountryLegalProfile> by lazy {
        val existingCodes = allCountries.map { it.code.uppercase(java.util.Locale.US) }.toSet()
        val additional = mutableListOf<CountryLegalProfile>()

        val isoCountries = java.util.Locale.getISOCountries()
        for (code in isoCountries) {
            if (!existingCodes.contains(code)) {
                try {
                    val profile = createDynamicProfile(code)
                    if (profile.name.isNotBlank() && profile.code.length == 2) {
                        additional.add(profile)
                    }
                } catch (_: Exception) {}
            }
        }
        allCountries + additional.sortedBy { it.name }
    }

    fun findByCode(code: String): CountryLegalProfile {
        val found = allCountries.find { it.code.equals(code, ignoreCase = true) }
        if (found != null) return found
        return try {
            createDynamicProfile(code)
        } catch (_: Exception) {
            allCountries.first()
        }
    }
}
