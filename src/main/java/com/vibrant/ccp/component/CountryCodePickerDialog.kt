package com.vibrant.ccp.component

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.vibrant.ccp.R
import com.vibrant.ccp.data.CountryData
import com.vibrant.ccp.data.utils.getCountryName
import com.vibrant.ccp.data.utils.getFlags
import com.vibrant.ccp.data.utils.getLibCountries
import com.vibrant.ccp.utils.searchCountry
import java.util.Locale

@Composable
fun CountryCodePickerDialog(
    modifier: Modifier = Modifier,
    padding: Dp = 15.dp,
    defaultSelectedCountry: CountryData = getLibCountries.first(),
    pickedCountry: (CountryData) -> Unit,
    showFlag: Boolean = true,
    ) {
    val context = LocalContext.current

    val countryList: List<CountryData> = getLibCountries
    var isPickCountry by remember {
        mutableStateOf(defaultSelectedCountry)
    }
    var isOpenDialog by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    Column(modifier = modifier
        .padding(padding)
        .clickable(
            interactionSource = interactionSource,
            indication = null,
        ) {
            isOpenDialog = true
        }) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showFlag) {
                Image(
                    modifier = modifier.width(26.dp),
                    painter = painterResource(
                        id = getFlags(
                            isPickCountry.countryCode
                        )
                    ), contentDescription = null
                )
            }
                Text(
                    text = " ${isPickCountry.countryCode.uppercase(Locale.getDefault())}  ${isPickCountry.countryPhoneCode}",
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(start = 6.dp),
                    fontSize = 18.sp,
                    color = MaterialTheme.colors.onSurface
                )

                Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
        }


        if (isOpenDialog) {
            CountryDialog(
                countryList = countryList,
                onDismissRequest = { isOpenDialog = false },
                context = context,
                dialogStatus = isOpenDialog,
                onSelected = { countryItem ->
                    pickedCountry(countryItem)
                    isPickCountry = countryItem
                    isOpenDialog = false
                }
            )
        }
    }
}

@Composable
fun CountryDialog(
    modifier: Modifier = Modifier,
    countryList: List<CountryData>,
    onDismissRequest: () -> Unit,
    onSelected: (item: CountryData) -> Unit,
    context: Context,
    dialogStatus: Boolean,
) {
    var searchValue by remember { mutableStateOf("") }
    if (!dialogStatus) searchValue = ""

    Dialog(
        onDismissRequest = onDismissRequest,
        content = {
            Surface(
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Scaffold { scaffold ->
                    scaffold.calculateBottomPadding()
                    Column(modifier = Modifier.fillMaxSize()) {
                        SearchTextField(
                            value = searchValue, onValueChange = { searchValue = it },
                            textColor = MaterialTheme.colors.onSurface,
                            fontSize = 16.sp,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Search,
                                    contentDescription = "Search",
                                    tint = MaterialTheme.colors.onSurface,
                                    modifier = Modifier.padding(horizontal = 10.dp)
                                )
                            },
                            modifier = Modifier
                                .clip(RoundedCornerShape(40))
                                .height(40.dp),
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        LazyColumn {
                            items(
                                if (searchValue.isEmpty()) countryList else countryList.searchCountry(
                                    searchValue,
                                    context
                                )
                            ) { countryItem ->
                                Row(
                                    Modifier
                                        .padding(18.dp)
                                        .fillMaxWidth()
                                        .clickable(onClick = { onSelected(countryItem) }),
                                    horizontalArrangement = Arrangement.Start,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Image(
                                        modifier = modifier.width(30.dp),
                                        painter = painterResource(
                                            id = getFlags(
                                                countryItem.countryCode
                                            )
                                        ), contentDescription = null
                                    )
                                    Text(
                                        stringResource(id = getCountryName(countryItem.countryCode.lowercase())),
                                        Modifier.padding(horizontal = 18.dp),
                                        fontSize = 14.sp,
                                        fontFamily = FontFamily(Font(R.font.poppins_medium)),
                                    )
                                }
                            }
                        }
                    }

                }

            }
        },
    )
}


@Composable
private fun SearchTextField(
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    value: String,
    textColor: Color = Color.Black,
    onValueChange: (String) -> Unit,
    hint: String = stringResource(id = R.string.search),
    fontSize: TextUnit = MaterialTheme.typography.body2.fontSize
) {
    BasicTextField(modifier = modifier
        .fillMaxWidth()
        .padding(horizontal = 18.dp),
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        cursorBrush = SolidColor(MaterialTheme.colors.primary),
        textStyle = LocalTextStyle.current.copy(
            color = textColor,
            fontSize = fontSize
        ),
        decorationBox = { innerTextField ->
            Row(
                modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (leadingIcon != null) leadingIcon()
                Box(Modifier.weight(1f)) {
                    if (value.isEmpty()) Text(
                        hint,
                        style = LocalTextStyle.current.copy(
                            color = textColor,
                            fontSize = fontSize
                        )
                    )
                    innerTextField()
                }
                if (trailingIcon != null) trailingIcon()
            }
        }
    )
}