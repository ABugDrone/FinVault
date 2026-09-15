package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CountryLegalProfile
import com.example.data.model.GlobalJurisdictions
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldIncome
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.VaultDarkBg

@Composable
fun PreSetupScreen(
    initialCountryCode: String = "US",
    isReconfiguring: Boolean = false,
    onCompleteSetup: (CountryLegalProfile) -> Unit,
    onCancelReconfiguration: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedRegion by remember { mutableStateOf("All") }
    var selectedCountry by remember {
        mutableStateOf(GlobalJurisdictions.findByCode(initialCountryCode))
    }
    var hasAcceptedLegalAccord by remember { mutableStateOf(false) }

    val regions = listOf("All", "Americas", "Europe", "Asia-Pacific", "Middle East & Africa")

    val filteredCountries by remember {
        derivedStateOf {
            GlobalJurisdictions.allCountries.filter { country ->
                val matchesRegion = selectedRegion == "All" || country.region.equals(selectedRegion, ignoreCase = true)
                val matchesQuery = searchQuery.isBlank() ||
                        country.name.contains(searchQuery, ignoreCase = true) ||
                        country.code.contains(searchQuery, ignoreCase = true) ||
                        country.currencyCode.contains(searchQuery, ignoreCase = true) ||
                        country.currencyName.contains(searchQuery, ignoreCase = true) ||
                        country.currencySymbol.contains(searchQuery, ignoreCase = true)
                matchesRegion && matchesQuery
            }
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("pre_setup_screen_root"),
        containerColor = VaultDarkBg,
        bottomBar = {
            // Bottom confirmation docking bar
            Surface(
                color = Color(0xFF090E17),
                tonalElevation = 8.dp,
                border = BorderStroke(1.dp, Color(0xFF1E293B)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 14.dp)
                ) {
                    // Legal Accord Checkbox
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (hasAcceptedLegalAccord) CyanAccent.copy(alpha = 0.08f) else Color.Transparent)
                            .clickable { hasAcceptedLegalAccord = !hasAcceptedLegalAccord }
                            .padding(vertical = 4.dp, horizontal = 6.dp)
                            .testTag("legal_accord_row"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = hasAcceptedLegalAccord,
                            onCheckedChange = { hasAcceptedLegalAccord = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = CyanAccent,
                                checkmarkColor = Color.Black,
                                uncheckedColor = MaterialTheme.colorScheme.outline
                            ),
                            modifier = Modifier.testTag("checkbox_legal_accord")
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "I legally designate ${selectedCountry.currencyName} (${selectedCountry.currencyCode} ${selectedCountry.currencySymbol}) under ${selectedCountry.name} as my sovereign ledger tender.",
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (hasAcceptedLegalAccord) Color.White else Color.LightGray.copy(alpha = 0.8f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (isReconfiguring && onCancelReconfiguration != null) {
                            Button(
                                onClick = onCancelReconfiguration,
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .testTag("btn_cancel_reconfiguration")
                            ) {
                                Text("Cancel", color = Color.White, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Button(
                            onClick = {
                                if (hasAcceptedLegalAccord) {
                                    onCompleteSetup(selectedCountry)
                                }
                            },
                            enabled = hasAcceptedLegalAccord,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CyanAccent,
                                disabledContainerColor = Color(0xFF1E293B)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(if (isReconfiguring) 2f else 1f)
                                .height(50.dp)
                                .testTag("btn_confirm_jurisdiction")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (hasAcceptedLegalAccord) Color.Black else Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isReconfiguring) "Update Legal Currency" else "Lock Jurisdiction & Enter Vault",
                                color = if (hasAcceptedLegalAccord) Color.Black else Color.Gray,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header with 3D Solid Icon & Title
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = Color(0xFF0C1420),
                        border = BorderStroke(1.5.dp, GoldAccent.copy(alpha = 0.6f)),
                        modifier = Modifier
                            .size(64.dp)
                            .shadow(elevation = 12.dp, shape = RoundedCornerShape(18.dp), spotColor = CyanAccent)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_app_icon_3d),
                            contentDescription = "FinVault 3D Emblem",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "JURISDICTION PRE-SETUP",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp,
                                color = GoldAccent
                            )
                        }
                        Text(
                            text = "Select Country & Currency",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "Your legal default currency for encrypted accounting",
                            fontSize = 12.sp,
                            color = Color.LightGray.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Legal Disclosure Warning Box
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF0D1B2A),
                    border = BorderStroke(1.dp, CyanAccent.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Gavel,
                            contentDescription = null,
                            tint = CyanAccent,
                            modifier = Modifier
                                .size(22.dp)
                                .padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Legal Currency Selection Accord",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyanAccent
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "By selecting your nation, you legally elect its sovereign currency as the operational denomination for all on-device ledger valuations, recurring obligations, budget thresholds, and tax-ready CSV/PDF reports. All financial records remain strictly hardware-encrypted on this device.",
                                fontSize = 11.sp,
                                lineHeight = 16.sp,
                                color = Color.LightGray.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }

            // Currently Selected Country Banner Card
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF131D2D),
                    border = BorderStroke(1.5.dp, Brush.horizontalGradient(listOf(CyanAccent, GoldAccent))),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("selected_country_banner")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "CURRENTLY SELECTED JURISDICTION",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp,
                                color = CyanAccent
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = EmeraldIncome.copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, EmeraldIncome.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = "LEGAL DEFAULT",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldIncome,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = selectedCountry.flagEmoji,
                                fontSize = 32.sp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = selectedCountry.name,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "${selectedCountry.currencyName} (${selectedCountry.currencyCode})",
                                    fontSize = 12.sp,
                                    color = Color.LightGray
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = CyanAccent.copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, CyanAccent.copy(alpha = 0.5f))
                            ) {
                                Text(
                                    text = selectedCountry.currencySymbol,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = CyanAccent,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = selectedCountry.regulatoryStandard,
                                fontSize = 10.sp,
                                color = GoldAccent,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Search Box
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search country, currency, ISO code (e.g. US, EUR, NGN, GBP)...", fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = CyanAccent
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear search", tint = Color.Gray)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanAccent,
                        unfocusedBorderColor = Color(0xFF1E293B),
                        focusedContainerColor = Color(0xFF0F1724),
                        unfocusedContainerColor = Color(0xFF0F1724)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_country_search")
                )
            }

            // Region filter chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(regions) { region ->
                        val isSelected = selectedRegion == region
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) CyanAccent else Color(0xFF131D2D),
                            border = BorderStroke(1.dp, if (isSelected) CyanAccent else Color(0xFF243248)),
                            modifier = Modifier
                                .clickable { selectedRegion = region }
                                .testTag("filter_region_$region")
                        ) {
                            Text(
                                text = region,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.Black else Color.White,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Header for Country list
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "GLOBAL JURISDICTIONS (${filteredCountries.size})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "Tap to choose",
                        fontSize = 11.sp,
                        color = Color.LightGray.copy(alpha = 0.6f)
                    )
                }
            }

            // List of countries
            items(filteredCountries, key = { it.code }) { country ->
                val isCurrent = country.code == selectedCountry.code
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isCurrent) Color(0xFF10233D) else Color(0xFF0F1725),
                    border = BorderStroke(
                        1.dp,
                        if (isCurrent) CyanAccent else Color(0xFF1D283A)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedCountry = country
                        }
                        .testTag("country_item_${country.code}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = country.flagEmoji,
                            fontSize = 24.sp
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = country.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFF1E293B)
                                ) {
                                    Text(
                                        text = country.code,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.LightGray,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = "${country.currencyName} • ${country.regulatoryStandard}",
                                fontSize = 11.sp,
                                color = Color.Gray,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Currency Code & Symbol Badge
                        Column(horizontalAlignment = Alignment.End) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isCurrent) CyanAccent else Color(0xFF1B283A),
                                border = BorderStroke(1.dp, if (isCurrent) CyanAccent else Color(0xFF2E3F58))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = country.currencyCode,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isCurrent) Color.Black else Color.White
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = country.currencySymbol,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isCurrent) Color.Black else CyanAccent
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Selection Check Icon
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(if (isCurrent) CyanAccent else Color(0xFF1A2637))
                                .border(1.dp, if (isCurrent) CyanAccent else Color(0xFF334155), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCurrent) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = Color.Black,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Extra space at bottom so list isn't hidden under bottomBar
            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
