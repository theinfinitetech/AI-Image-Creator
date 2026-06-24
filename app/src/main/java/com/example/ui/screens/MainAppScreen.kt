package com.example.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.Comment
import com.example.data.model.Creation
import com.example.ui.viewmodel.ArtStyle
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Custom Cosmic Slate Palette Colors
val CosmicSlateBg = Color(0xFF111318)      // Immersive deep black backdrop
val CosmicCardBg = Color(0xFF1D1B20)       // Dark premium card/container background
val CosmicCyanAccent = Color(0xFFD0BCFF)   // Glowing electric lavender/cyan accent
val CosmicPurple = Color(0xFF381E72)       // Rich deep active purple
val CosmicMutedText = Color(0xFF939099)    // Soft muted gray text
val CosmicWhiteText = Color(0xFFE2E2E6)    // Crisp off-white premium text

@Composable
fun MainAppScreen(viewModel: MainViewModel) {
    var selectedTab by remember { mutableStateOf(0) }
    val walletState by viewModel.wallet.collectAsState()
    val context = LocalContext.current
    var activeAddCollectionCreation by remember { mutableStateOf<Creation?>(null) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = CosmicSlateBg,
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .navigationBarsPadding()
                    .testTag("bottom_nav_bar"),
                containerColor = CosmicCardBg,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Brush, contentDescription = "Create Tab") },
                    label = { Text("Create", fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CosmicSlateBg,
                        selectedTextColor = CosmicCyanAccent,
                        indicatorColor = CosmicCyanAccent,
                        unselectedIconColor = CosmicMutedText,
                        unselectedTextColor = CosmicMutedText
                    ),
                    modifier = Modifier.testTag("tab_create")
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Explore, contentDescription = "Community Feed Tab") },
                    label = { Text("Feed", fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CosmicSlateBg,
                        selectedTextColor = CosmicCyanAccent,
                        indicatorColor = CosmicCyanAccent,
                        unselectedIconColor = CosmicMutedText,
                        unselectedTextColor = CosmicMutedText
                    ),
                    modifier = Modifier.testTag("tab_feed")
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Redeem, contentDescription = "Wallet Rewards Tab") },
                    label = { Text("Rewards", fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CosmicSlateBg,
                        selectedTextColor = CosmicCyanAccent,
                        indicatorColor = CosmicCyanAccent,
                        unselectedIconColor = CosmicMutedText,
                        unselectedTextColor = CosmicMutedText
                    ),
                    modifier = Modifier.testTag("tab_rewards")
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.PhotoLibrary, contentDescription = "Collections Tab") },
                    label = { Text("Collections", fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CosmicSlateBg,
                        selectedTextColor = CosmicCyanAccent,
                        indicatorColor = CosmicCyanAccent,
                        unselectedIconColor = CosmicMutedText,
                        unselectedTextColor = CosmicMutedText
                    ),
                    modifier = Modifier.testTag("tab_collections")
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // High-fidelity App Header
            AppHeader(
                credits = walletState?.credits ?: 0,
                onClaimClicked = { selectedTab = 2 }
            )

            // Current Tab Content
            Box(modifier = Modifier.fillMaxSize()) {
                when (selectedTab) {
                    0 -> CreateArtTab(viewModel)
                    1 -> CommunityFeedTab(viewModel)
                    2 -> RewardsAndHistoryTab(viewModel, onAddToCollection = { activeAddCollectionCreation = it })
                    3 -> CollectionsTab(viewModel)
                }
            }
        }
    }

    // Modal dialog to add selected creation to user's custom collections
    if (activeAddCollectionCreation != null) {
        AddImageToCollectionDialog(
            creation = activeAddCollectionCreation!!,
            viewModel = viewModel,
            onDismissRequest = { activeAddCollectionCreation = null }
        )
    }
}

@Composable
fun AppHeader(credits: Int, onClaimClicked: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .background(CosmicCardBg)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Elegant gradient logo from the Immersive UI theme
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFFD0BCFF), Color(0xFF381E72))
                        ),
                        CircleShape
                    )
                    .shadow(elevation = 12.dp, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "A",
                    color = Color(0xFFE2E2E6),
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "AI IMAGE CREATOR",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = CosmicWhiteText,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Powered by Gemini AI",
                    fontSize = 11.sp,
                    color = CosmicMutedText,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Beautiful Credit Balance Button/Chip
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(CosmicSlateBg)
                .clickable { onClaimClicked() }
                .border(1.dp, CosmicCyanAccent.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Wallet,
                contentDescription = "Credits Balance",
                tint = CosmicCyanAccent,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "$credits cr",
                fontWeight = FontWeight.Bold,
                color = CosmicWhiteText,
                fontSize = 14.sp,
                modifier = Modifier.testTag("credits_balance_header")
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "+",
                fontWeight = FontWeight.Bold,
                color = CosmicCyanAccent,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun CreateArtTab(viewModel: MainViewModel) {
    val prompt by viewModel.prompt.collectAsState()
    val selectedStyle by viewModel.selectedStyle.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val recentlyGenerated by viewModel.recentlyGenerated.collectAsState()
    val generationError by viewModel.generationError.collectAsState()

    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // Error Alert Box
    LaunchedEffect(generationError) {
        if (generationError != null) {
            // Dismiss error automatically or keep active
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                // Aesthetic Banner
                ArtBanner()
            }

            item {
                Text(
                    text = "1. Express your vision",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = CosmicWhiteText,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                // High-End Outlined Prompt Text Field
                OutlinedTextField(
                    value = prompt,
                    onValueChange = { viewModel.onPromptChanged(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(110.dp)
                        .testTag("prompt_input_field"),
                    placeholder = {
                        Text(
                            text = "Describe anything you want to create (e.g. A Chibi orange kitten sleeping inside a magical golden cup...)",
                            color = CosmicMutedText.copy(alpha = 0.8f),
                            fontSize = 13.sp
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = CosmicWhiteText,
                        unfocusedTextColor = CosmicWhiteText,
                        focusedBorderColor = CosmicCyanAccent,
                        unfocusedBorderColor = CosmicCardBg,
                        focusedContainerColor = CosmicCardBg,
                        unfocusedContainerColor = CosmicCardBg
                    ),
                    shape = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { keyboardController?.hide() }
                    )
                )

                // Prompt Utilities Row (Clear, Random ideas, and AI Enhance)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Predefined fun creative prompts suggestions
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(CosmicCardBg)
                            .clickable {
                                val ideas = listOf(
                                    "A futuristic glass palace in clouds",
                                    "Sleeping tiny galaxy wolf cub",
                                    "Ancient library flooded with bioluminescent flowers",
                                    "A magical flying train traveling over green hills",
                                    "Retro polaroid of an astronaut camping in woods"
                                )
                                viewModel.onPromptChanged(ideas.random())
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = "Random Idea",
                            tint = CosmicPurple,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Random Idea",
                            color = CosmicMutedText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Gemini Prompt Enhancer Button
                    Button(
                        onClick = {
                            if (prompt.isNotBlank()) {
                                keyboardController?.hide()
                                viewModel.enhancePrompt(prompt, selectedStyle.name) { enhanced ->
                                    viewModel.onPromptChanged(enhanced)
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CosmicPurple),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("enhance_prompt_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Enhance via Gemini",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Gemini Enhance ✨", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "2. Select Style",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = CosmicWhiteText,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                // Style Gallery Slider
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(viewModel.styles) { style ->
                        StyleCard(
                            style = style,
                            isSelected = selectedStyle.name == style.name,
                            onClick = { viewModel.onStyleSelected(style) }
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))

                // Error Box if any
                if (generationError != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF7F1D1D)),
                        border = border(Color.Red.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Error",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = generationError ?: "",
                                color = Color.White,
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { viewModel.dismissError() }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Dismiss",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                // Core Generate Action Button (Costs 5 Credits)
                Button(
                    onClick = {
                        keyboardController?.hide()
                        viewModel.generateImage()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(56.dp)
                        .shadow(12.dp, RoundedCornerShape(16.dp))
                        .testTag("generate_image_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CosmicCyanAccent,
                        contentColor = CosmicSlateBg
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Memory,
                        contentDescription = "Create Artwork",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "GENERATE ARTWORK",
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(CosmicSlateBg.copy(alpha = 0.25f))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⚡ 5 Cr",
                            fontWeight = FontWeight.Bold,
                            color = CosmicSlateBg,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Loading and Generation Shimmer Overlay Dialog
        AnimatedVisibility(
            visible = isGenerating,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            GeneratingOverlay()
        }

        // Completed Creation Modal Dialog popup
        if (recentlyGenerated != null) {
            CompletedArtDialog(
                creation = recentlyGenerated!!,
                onDismiss = { viewModel.clearRecentlyGenerated() },
                onShareToCommunity = {
                    viewModel.shareCreationToCommunity(recentlyGenerated!!.id)
                },
                onSocialShare = {
                    shareImageText(context, recentlyGenerated!!.prompt, recentlyGenerated!!.imageUrl)
                }
            )
        }
    }
}

@Composable
fun ArtBanner() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(130.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CosmicCardBg),
        border = border(CosmicCyanAccent.copy(alpha = 0.2f))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Ambient Decorative Art Graphic Background using simple Canvas
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1.3f)) {
                    Text(
                        text = "Unleash Your Imagination",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = CosmicCyanAccent
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Turn raw text into gorgeous high-fidelity art with advanced AI style maps.",
                        fontSize = 11.sp,
                        color = CosmicMutedText,
                        lineHeight = 15.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Free daily claims! • 5 credits per draft",
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = CosmicPurple
                    )
                }

                // Miniature cute preview box
                Box(
                    modifier = Modifier
                        .weight(0.7f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CosmicSlateBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Decoration",
                        tint = CosmicCyanAccent.copy(alpha = 0.7f),
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun StyleCard(
    style: ArtStyle,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) CosmicCyanAccent else Color.Transparent,
        label = "BorderColor"
    )

    val scaleValue by animateColorAsState(
        targetValue = if (isSelected) CosmicCyanAccent else CosmicCardBg,
        label = "BgColor"
    )

    Column(
        modifier = Modifier
            .width(84.dp)
            .clickable { onClick() }
            .testTag("style_card_${style.name}"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(scaleValue)
                .border(2.dp, borderColor, RoundedCornerShape(16.dp))
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(style.thumbnail)
                    .crossfade(true)
                    .build(),
                contentDescription = style.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(CosmicCyanAccent.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Selected",
                        tint = CosmicWhiteText,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = style.name,
            color = if (isSelected) CosmicCyanAccent else CosmicWhiteText,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun GeneratingOverlay() {
    var activeTip by remember { mutableStateOf("Prepping canvas elements...") }
    val tips = listOf(
        "Formulating style layers...",
        "Calling Gemini prompt enhancers...",
        "Applying deep watercolor washes...",
        "Adding cinematic volumetric glow...",
        "Brushing beautiful textures...",
        "Constructing cyber neon lighting...",
        "Finalizing stunning artistic render..."
    )

    LaunchedEffect(Unit) {
        while (true) {
            delay(2000)
            activeTip = tips.random()
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "Overlay")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Restart
        ),
        label = "Rotate"
    )

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Semi-transparent background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.85f)
                    .background(CosmicSlateBg)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(72.dp)
                            .scale(1.2f),
                        color = CosmicCyanAccent,
                        strokeWidth = 3.dp
                    )
                    Icon(
                        imageVector = Icons.Default.Brush,
                        contentDescription = "Generator Brush",
                        tint = CosmicPurple,
                        modifier = Modifier
                            .size(28.dp)
                            .scale(1f)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "GENERATING ARTWORK",
                    fontWeight = FontWeight.Black,
                    color = CosmicWhiteText,
                    fontSize = 16.sp,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = activeTip,
                    color = CosmicCyanAccent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Generating images for free using Pollinations API and Google Gemini API.",
                    color = CosmicMutedText,
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 48.dp)
                )
            }
        }
    }
}

@Composable
fun CompletedArtDialog(
    creation: Creation,
    onDismiss: () -> Unit,
    onShareToCommunity: () -> Unit,
    onSocialShare: () -> Unit
) {
    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(CosmicSlateBg),
            color = CosmicSlateBg
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Masterpiece Complete! 🎉",
                        fontWeight = FontWeight.Black,
                        color = CosmicCyanAccent,
                        fontSize = 18.sp
                    )

                    IconButton(
                        onClick = { onDismiss() },
                        modifier = Modifier
                            .background(CosmicCardBg, CircleShape)
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Dialog",
                            tint = CosmicWhiteText
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Image display container
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .shadow(16.dp)
                        .background(CosmicCardBg)
                        .border(1.dp, CosmicCyanAccent.copy(alpha = 0.3f), RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(creation.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = creation.prompt,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Detail display box
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CosmicCardBg),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "STYLE: ",
                                fontWeight = FontWeight.Bold,
                                color = CosmicCyanAccent,
                                fontSize = 10.sp
                            )
                            Text(
                                text = creation.styleName.uppercase(),
                                fontWeight = FontWeight.ExtraBold,
                                color = CosmicWhiteText,
                                fontSize = 10.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = creation.prompt,
                            color = CosmicWhiteText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Sharing Button Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Showcase to Feed Button
                    Button(
                        onClick = {
                            onShareToCommunity()
                            onDismiss()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("share_community_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (creation.isShared) CosmicMutedText else CosmicPurple
                        ),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !creation.isShared
                    ) {
                        Icon(
                            imageVector = Icons.Default.Explore,
                            contentDescription = "Showcase art"
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (creation.isShared) "Shared!" else "Showcase Feed",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    // Social Share Button
                    Button(
                        onClick = { onSocialShare() },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("social_share_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CosmicCyanAccent,
                            contentColor = CosmicSlateBg
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Social Share"
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Share Out",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Save locally info label
                Text(
                    text = "Saved automatically in your creation History! 💾",
                    color = CosmicMutedText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun CommunityFeedTab(viewModel: MainViewModel) {
    val sharedPosts by viewModel.sharedCreations.collectAsState()
    val activeCommentsPostId by viewModel.activeCommentPostId.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        if (sharedPosts.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Explore,
                    contentDescription = "Empty Feed",
                    tint = CosmicMutedText.copy(alpha = 0.5f),
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "The gallery is empty!",
                    color = CosmicWhiteText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "Generate and showcase your artwork to fill up the feed.",
                    color = CosmicMutedText,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(sharedPosts) { post ->
                    CommunityPostItem(
                        post = post,
                        onLikeClicked = { viewModel.toggleLike(post.id) },
                        onCommentClicked = { viewModel.openCommentsForPost(post.id) }
                    )
                }
            }
        }

        // Nested Comment Bottom Sheet Panel
        if (activeCommentsPostId != null) {
            CommentSheet(
                postId = activeCommentsPostId!!,
                comments = viewModel.activeComments.collectAsState().value,
                onDismiss = { viewModel.openCommentsForPost(null) },
                onSubmitComment = { text -> viewModel.submitComment(activeCommentsPostId!!, text) }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CommunityPostItem(
    post: Creation,
    onLikeClicked: () -> Unit,
    onCommentClicked: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .shadow(4.dp, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = CosmicCardBg),
        shape = RoundedCornerShape(20.dp),
        border = border(CosmicMutedText.copy(alpha = 0.15f))
    ) {
        Column {
            // Author row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Author Avatar circle placeholder
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(CosmicPurple, CosmicCyanAccent)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = post.authorName.take(1).uppercase(),
                        color = CosmicWhiteText,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = post.authorName,
                        fontWeight = FontWeight.Bold,
                        color = CosmicWhiteText,
                        fontSize = 14.sp
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "STYLE: ",
                            fontSize = 9.sp,
                            color = CosmicCyanAccent,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = post.styleName.uppercase(),
                            fontSize = 9.sp,
                            color = CosmicMutedText,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Time label
                val ago = remember(post.timestamp) {
                    val minutes = (System.currentTimeMillis() - post.timestamp) / (1000 * 60)
                    when {
                        minutes < 1 -> "Just now"
                        minutes < 60 -> "$minutes mins ago"
                        minutes < 1440 -> "${minutes / 60} hours ago"
                        else -> "${minutes / 1440} days ago"
                    }
                }
                Text(
                    text = ago,
                    fontSize = 10.sp,
                    color = CosmicMutedText
                )
            }

            // Image Frame with double-tap support
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(CosmicSlateBg)
                    .combinedClickable(
                        onDoubleClick = { onLikeClicked() },
                        onClick = {}
                    ),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(post.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = post.prompt,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Prompt description text
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                Text(
                    text = post.prompt,
                    color = CosmicWhiteText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 18.sp
                )
                if (post.enhancedPrompt.isNotEmpty() && post.enhancedPrompt != post.prompt) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = post.enhancedPrompt,
                        color = CosmicMutedText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 15.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Likes and Comments Action Buttons Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Like Button
                IconButton(onClick = { onLikeClicked() }) {
                    Icon(
                        imageVector = if (post.isLikedByMe) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like art",
                        tint = if (post.isLikedByMe) Color.Red else CosmicWhiteText
                    )
                }
                Text(
                    text = "${post.likes}",
                    fontWeight = FontWeight.Bold,
                    color = CosmicWhiteText,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Comments Button
                IconButton(onClick = { onCommentClicked() }) {
                    Icon(
                        imageVector = Icons.Default.Forum,
                        contentDescription = "Comments board",
                        tint = CosmicWhiteText
                    )
                }
                Text(
                    text = "Comments",
                    fontWeight = FontWeight.Bold,
                    color = CosmicWhiteText,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentSheet(
    postId: Int,
    comments: List<Comment>,
    onDismiss: () -> Unit,
    onSubmitComment: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var textInput by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        containerColor = CosmicCardBg,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(CosmicMutedText)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.75f)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Comments Feed",
                fontWeight = FontWeight.Black,
                color = CosmicWhiteText,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Comments List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (comments.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "No comments yet.",
                                color = CosmicMutedText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Be the first to share your thoughts!",
                                color = CosmicMutedText,
                                fontSize = 11.sp
                            )
                        }
                    }
                } else {
                    items(comments) { comment ->
                        CommentItem(comment = comment)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Text input row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    placeholder = { Text("Write a comment...", fontSize = 13.sp, color = CosmicMutedText) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("comment_input_field"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = CosmicWhiteText,
                        unfocusedTextColor = CosmicWhiteText,
                        focusedBorderColor = CosmicCyanAccent,
                        unfocusedBorderColor = CosmicSlateBg,
                        focusedContainerColor = CosmicSlateBg,
                        unfocusedContainerColor = CosmicSlateBg
                    ),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (textInput.isNotBlank()) {
                            onSubmitComment(textInput)
                            textInput = ""
                        }
                    },
                    modifier = Modifier
                        .background(CosmicCyanAccent, CircleShape)
                        .size(44.dp)
                        .testTag("submit_comment_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Post Comment",
                        tint = CosmicSlateBg
                    )
                }
            }
        }
    }
}

@Composable
fun CommentItem(comment: Comment) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CosmicSlateBg)
            .padding(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Miniature Comment Avatar
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(CosmicPurple),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = comment.authorName.take(1).uppercase(),
                color = CosmicWhiteText,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = comment.authorName,
                    color = CosmicCyanAccent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = remember(comment.timestamp) {
                        val passed = (System.currentTimeMillis() - comment.timestamp) / (1000 * 60)
                        if (passed < 1) "now" else "$passed m ago"
                    },
                    color = CosmicMutedText,
                    fontSize = 9.sp
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = comment.text,
                color = CosmicWhiteText,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun RewardsAndHistoryTab(viewModel: MainViewModel, onAddToCollection: (Creation) -> Unit) {
    val wallet by viewModel.wallet.collectAsState()
    val myHistoryList by viewModel.myHistory.collectAsState()
    val rewardStatusMessage by viewModel.rewardStatus.collectAsState()
    val remainingMillis by viewModel.remainingClaimMillis.collectAsState()

    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Claim Rewards Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CosmicCardBg),
                shape = RoundedCornerShape(24.dp),
                border = border(CosmicPurple.copy(alpha = 0.25f))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Redeem,
                        contentDescription = "Daily Rewards Claim",
                        tint = CosmicCyanAccent,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Claim Daily Credits",
                        fontWeight = FontWeight.Black,
                        color = CosmicWhiteText,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "Claim +20 free creation credits once every 24 hours to keep building artwork!",
                        color = CosmicMutedText,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 15.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Timer or Ready badge
                    val isClaimReady = remainingMillis <= 0
                    if (isClaimReady) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(CosmicCyanAccent.copy(alpha = 0.15f))
                                .border(1.dp, CosmicCyanAccent, RoundedCornerShape(8.dp))
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                "DAILY GIFT READY! 🎁",
                                color = CosmicCyanAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        val hours = remainingMillis / (1000 * 60 * 60)
                        val minutes = (remainingMillis % (1000 * 60 * 60)) / (1000 * 60)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(CosmicSlateBg)
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                "Next gift in: ${hours}h ${minutes}m",
                                color = CosmicMutedText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Claim Action Button
                    Button(
                        onClick = { viewModel.claimDailyCredits() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isClaimReady) CosmicCyanAccent else CosmicCardBg,
                            contentColor = if (isClaimReady) CosmicSlateBg else CosmicMutedText
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("claim_rewards_button"),
                        border = if (isClaimReady) null else border(CosmicMutedText.copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (isClaimReady) "CLAIM +20 CREDITS FREE" else "LOCKED UNTIL TOMORROW",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        )
                    }

                    // Alert Messages for Reward operations
                    if (rewardStatusMessage != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = CosmicSlateBg)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = rewardStatusMessage ?: "",
                                    color = CosmicWhiteText,
                                    fontSize = 11.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(onClick = { viewModel.dismissRewardStatus() }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Dismiss status",
                                        tint = CosmicCyanAccent,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. Personal History List
        item {
            Text(
                text = "Your Studio Gallery (${myHistoryList.size})",
                fontWeight = FontWeight.Black,
                color = CosmicWhiteText,
                fontSize = 16.sp
            )
        }

        if (myHistoryList.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Brush,
                        contentDescription = "No local history",
                        tint = CosmicMutedText.copy(alpha = 0.3f),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Your studio collection is empty.",
                        color = CosmicMutedText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Go back to the Create tab and try out style renders!",
                        color = CosmicMutedText,
                        fontSize = 11.sp
                    )
                }
            }
        } else {
            // Displaying User Studio Art Gallery in a clean grid list
            item {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp), // Fix height to scroll elegantly inside LazyColumn
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(myHistoryList) { creation ->
                        HistoryGridItem(
                            creation = creation,
                            onShareClicked = {
                                viewModel.shareCreationToCommunity(creation.id)
                            },
                            onDeleteClicked = {
                                viewModel.deleteCreation(creation.id)
                            },
                            onSocialExportClicked = {
                                shareImageText(context, creation.prompt, creation.imageUrl)
                            },
                            onAddToCollectionClicked = {
                                onAddToCollection(creation)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryGridItem(
    creation: Creation,
    onShareClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
    onSocialExportClicked: () -> Unit,
    onAddToCollectionClicked: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(210.dp),
        colors = CardDefaults.cardColors(containerColor = CosmicCardBg),
        shape = RoundedCornerShape(16.dp),
        border = border(CosmicMutedText.copy(alpha = 0.15f))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1.2f)
                    .fillMaxWidth()
                    .background(CosmicSlateBg)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(creation.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = creation.prompt,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Shared Status Tag on Image
                if (creation.isShared) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(CosmicPurple)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "SHARED",
                            fontWeight = FontWeight.Black,
                            fontSize = 8.sp,
                            color = CosmicWhiteText
                        )
                    }
                }
            }

            // Controls row and details
            Column(
                modifier = Modifier
                    .weight(0.8f)
                    .fillMaxWidth()
                    .padding(6.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = creation.prompt,
                    color = CosmicWhiteText,
                    fontSize = 11.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 14.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Native export
                        IconButton(
                            onClick = onSocialExportClicked,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Social Share",
                                tint = CosmicCyanAccent,
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        // Add to collection folder
                        IconButton(
                            onClick = onAddToCollectionClicked,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Folder,
                                contentDescription = "Add to Collection",
                                tint = CosmicCyanAccent,
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        // Showcase share
                        if (!creation.isShared) {
                            IconButton(
                                onClick = onShareClicked,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Explore,
                                    contentDescription = "Showcase art",
                                    tint = CosmicPurple,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }

                    // Delete art
                    IconButton(
                        onClick = onDeleteClicked,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete art",
                            tint = Color.Red.copy(alpha = 0.8f),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

// Convenient custom border drawer
@Composable
fun border(color: Color) = androidx.compose.foundation.BorderStroke(1.dp, color)

// Helper sharing text/intent
fun shareImageText(context: Context, prompt: String, imageUrl: String) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, "Look at this amazing AI art I created: \"$prompt\"\n\nGenerated for FREE via AI Image Creator!\nLink: $imageUrl")
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "Share your artwork")
    context.startActivity(shareIntent)
}

@Composable
fun AddImageToCollectionDialog(
    creation: Creation,
    viewModel: MainViewModel,
    onDismissRequest: () -> Unit
) {
    val collections by viewModel.allCollections.collectAsState()
    var newCollectionName by remember { mutableStateOf("") }
    var newCollectionDesc by remember { mutableStateOf("") }
    var showCreateForm by remember { mutableStateOf(false) }

    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismissRequest,
        containerColor = CosmicCardBg,
        title = {
            Text(
                "Organize Art",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = CosmicCyanAccent
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
            ) {
                Text(
                    text = "Add this artwork to an existing collection or create a new one.",
                    fontSize = 12.sp,
                    color = CosmicMutedText,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                if (collections.isEmpty() && !showCreateForm) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No collections created yet.",
                            color = CosmicMutedText,
                            fontSize = 13.sp,
                            fontStyle = FontStyle.Italic
                        )
                    }
                } else if (!showCreateForm) {
                    // List of existing collections
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp)
                    ) {
                        items(collections) { collection ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.addCreationToCollection(collection.id, creation.id)
                                        Toast.makeText(context, "Added to '${collection.name}'", Toast.LENGTH_SHORT).show()
                                        onDismissRequest()
                                    }
                                    .padding(vertical = 10.dp, horizontal = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Folder,
                                        contentDescription = "Collection",
                                        tint = CosmicCyanAccent,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = collection.name,
                                            color = CosmicWhiteText,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (collection.description.isNotEmpty()) {
                                            Text(
                                                text = collection.description,
                                                color = CosmicMutedText,
                                                fontSize = 11.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                                Icon(
                                    imageVector = Icons.Default.AddCircle,
                                    contentDescription = "Add",
                                    tint = CosmicCyanAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            HorizontalDivider(color = CosmicMutedText.copy(alpha = 0.1f))
                        }
                    }
                }

                if (showCreateForm) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newCollectionName,
                        onValueChange = { newCollectionName = it },
                        label = { Text("Collection Name", color = CosmicMutedText) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = CosmicWhiteText,
                            unfocusedTextColor = CosmicWhiteText,
                            focusedBorderColor = CosmicCyanAccent,
                            unfocusedBorderColor = CosmicMutedText.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newCollectionDesc,
                        onValueChange = { newCollectionDesc = it },
                        label = { Text("Description (Optional)", color = CosmicMutedText) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = CosmicWhiteText,
                            unfocusedTextColor = CosmicWhiteText,
                            focusedBorderColor = CosmicCyanAccent,
                            unfocusedBorderColor = CosmicMutedText.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }
        },
        confirmButton = {
            if (showCreateForm) {
                Button(
                    onClick = {
                        if (newCollectionName.isNotBlank()) {
                            viewModel.createCollection(newCollectionName, newCollectionDesc)
                            newCollectionName = ""
                            newCollectionDesc = ""
                            showCreateForm = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CosmicCyanAccent, contentColor = CosmicSlateBg)
                ) {
                    Text("Save Collection", fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = { showCreateForm = true },
                    colors = ButtonDefaults.buttonColors(containerColor = CosmicPurple, contentColor = Color.White)
                ) {
                    Text("+ New Collection", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    if (showCreateForm) {
                        showCreateForm = false
                    } else {
                        onDismissRequest()
                    }
                }
            ) {
                Text("Cancel", color = CosmicMutedText)
            }
        }
    )
}

@Composable
fun CollectionsTab(viewModel: MainViewModel) {
    val collections by viewModel.allCollections.collectAsState()
    val selectedCollection by viewModel.selectedCollection.collectAsState()
    val creationsInCollection by viewModel.creationsInSelectedCollection.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var newCollName by remember { mutableStateOf("") }
    var newCollDesc by remember { mutableStateOf("") }

    val context = LocalContext.current

    if (selectedCollection != null) {
        val collection = selectedCollection!!
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header / Back navigation
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.selectCollection(null) },
                    modifier = Modifier
                        .size(36.dp)
                        .background(CosmicCardBg, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = CosmicCyanAccent,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = collection.name.uppercase(),
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = CosmicCyanAccent,
                        letterSpacing = 0.5.sp
                    )
                    if (collection.description.isNotEmpty()) {
                        Text(
                            text = collection.description,
                            color = CosmicMutedText,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Delete collection button
                IconButton(
                    onClick = {
                        viewModel.deleteCollection(collection.id)
                        Toast.makeText(context, "Collection deleted", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFF7F1D1D).copy(alpha = 0.3f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Collection",
                        tint = Color.Red,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (creationsInCollection.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = "Empty Collection",
                            tint = CosmicMutedText.copy(alpha = 0.3f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "This collection is empty.",
                            color = CosmicMutedText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Go to 'Rewards' tab to add your creations!",
                            color = CosmicMutedText.copy(alpha = 0.7f),
                            fontSize = 11.sp
                        )
                    }
                }
            } else {
                // Show grid of images in collection
                var activeDetailCreation by remember { mutableStateOf<Creation?>(null) }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(creationsInCollection) { creation ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clickable { activeDetailCreation = creation },
                            colors = CardDefaults.cardColors(containerColor = CosmicCardBg),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(creation.imageUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = creation.prompt,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )

                                // Overlay gradient and title
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.BottomCenter)
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                                            )
                                        )
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = creation.prompt,
                                        color = CosmicWhiteText,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }

                // Show detail dialog if clicked
                if (activeDetailCreation != null) {
                    val detailCreation = activeDetailCreation!!
                    AlertDialog(
                        onDismissRequest = { activeDetailCreation = null },
                        containerColor = CosmicCardBg,
                        title = {
                            Text(
                                text = "Artwork Details",
                                color = CosmicWhiteText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        },
                        text = {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(CosmicSlateBg)
                                ) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(detailCreation.imageUrl)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = detailCreation.prompt,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Prompt:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = CosmicCyanAccent
                                )
                                Text(
                                    text = detailCreation.prompt,
                                    color = CosmicWhiteText,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Text(
                                    text = "Enhanced Prompt:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = CosmicCyanAccent
                                )
                                Text(
                                    text = detailCreation.enhancedPrompt,
                                    color = CosmicMutedText,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Text(
                                    text = "Style: ${detailCreation.styleName}",
                                    color = CosmicWhiteText,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    viewModel.removeCreationFromCollection(collection.id, detailCreation.id)
                                    activeDetailCreation = null
                                    Toast.makeText(context, "Removed from collection", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7F1D1D), contentColor = Color.White)
                            ) {
                                Text("Remove from Collection", fontWeight = FontWeight.Bold)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { activeDetailCreation = null }) {
                                Text("Close", color = CosmicCyanAccent)
                            }
                        }
                    )
                }
            }
        }
    } else {
        // Render Collection Grid List
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "MY COLLECTIONS",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = CosmicCyanAccent,
                            letterSpacing = 1.sp
                        )
                        Button(
                            onClick = { showCreateDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = CosmicPurple),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CreateNewFolder,
                                contentDescription = "Create",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("NEW", fontSize = 11.sp, fontWeight = FontWeight.Black)
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Curate and organize your best creations by theme, style, or subject.",
                        fontSize = 11.sp,
                        color = CosmicMutedText,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                if (collections.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.FolderOpen,
                                    contentDescription = "No Collections",
                                    tint = CosmicMutedText.copy(alpha = 0.2f),
                                    modifier = Modifier.size(72.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "No collections yet.",
                                    color = CosmicMutedText,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Tap 'NEW' above to create your first collection!",
                                    color = CosmicMutedText.copy(alpha = 0.7f),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                } else {
                    items(collections) { collection ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.selectCollection(collection) },
                            colors = CardDefaults.cardColors(containerColor = CosmicCardBg),
                            shape = RoundedCornerShape(16.dp),
                            border = border(CosmicMutedText.copy(alpha = 0.15f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(CosmicPurple.copy(alpha = 0.2f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PhotoLibrary,
                                        contentDescription = "Collection Icon",
                                        tint = CosmicCyanAccent,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = collection.name,
                                        color = CosmicWhiteText,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (collection.description.isNotEmpty()) {
                                        Text(
                                            text = collection.description,
                                            color = CosmicMutedText,
                                            fontSize = 11.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowRight,
                                    contentDescription = "Open",
                                    tint = CosmicMutedText,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Create Dialog
            if (showCreateDialog) {
                AlertDialog(
                    onDismissRequest = { showCreateDialog = false },
                    containerColor = CosmicCardBg,
                    title = {
                        Text(
                            "New Collection",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = CosmicWhiteText
                        )
                    },
                    text = {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = newCollName,
                                onValueChange = { newCollName = it },
                                label = { Text("Name (e.g. Dreamscapes)", color = CosmicMutedText) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = CosmicWhiteText,
                                    unfocusedTextColor = CosmicWhiteText,
                                    focusedBorderColor = CosmicCyanAccent,
                                    unfocusedBorderColor = CosmicMutedText.copy(alpha = 0.3f)
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = newCollDesc,
                                onValueChange = { newCollDesc = it },
                                label = { Text("Description (e.g. My surreal artwork)", color = CosmicMutedText) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = CosmicWhiteText,
                                    unfocusedTextColor = CosmicWhiteText,
                                    focusedBorderColor = CosmicCyanAccent,
                                    unfocusedBorderColor = CosmicMutedText.copy(alpha = 0.3f)
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (newCollName.isNotBlank()) {
                                    viewModel.createCollection(newCollName, newCollDesc)
                                    newCollName = ""
                                    newCollDesc = ""
                                    showCreateDialog = false
                                    Toast.makeText(context, "Collection created!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CosmicCyanAccent, contentColor = CosmicSlateBg)
                        ) {
                            Text("Create", fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showCreateDialog = false }) {
                            Text("Cancel", color = CosmicMutedText)
                        }
                    }
                )
            }
        }
    }
}
