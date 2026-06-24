package com.example.data.repository

import android.util.Log
import com.example.BuildConfig
import com.example.data.api.Content
import com.example.data.api.GenerateContentRequest
import com.example.data.api.Part
import com.example.data.api.RetrofitClient
import com.example.data.database.CommentDao
import com.example.data.database.CreationDao
import com.example.data.database.UserWalletDao
import com.example.data.model.Comment
import com.example.data.model.Creation
import com.example.data.model.UserWallet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URLEncoder

import com.example.data.database.CollectionDao
import com.example.data.model.CollectionCreationCrossRef
import com.example.data.model.CollectionEntity

class ArtRepository(
    private val walletDao: UserWalletDao,
    private val creationDao: CreationDao,
    private val commentDao: CommentDao,
    private val collectionDao: CollectionDao,
    private val externalScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {

    val walletFlow: Flow<UserWallet?> = walletDao.getWalletFlow()
    val allCreations: Flow<List<Creation>> = creationDao.getAllCreations()
    val sharedCreations: Flow<List<Creation>> = creationDao.getSharedCreations()
    val allCollections: Flow<List<CollectionEntity>> = collectionDao.getAllCollections()

    init {
        // Run database pre-population and initialization on a background thread
        externalScope.launch {
            initWallet()
            prepopulateDatabaseIfEmpty()
        }
    }

    private suspend fun initWallet() {
        val existing = walletDao.getWallet()
        if (existing == null) {
            walletDao.insertWallet(UserWallet(id = 1, credits = 50, lastClaimedAt = 0L))
        }
    }

    fun getCommentsForCreation(creationId: Int): Flow<List<Comment>> {
        return commentDao.getCommentsForCreationFlow(creationId)
    }

    suspend fun getCreationById(creationId: Int): Creation? {
        return creationDao.getCreationById(creationId)
    }

    /**
     * Deducts credit cost (e.g., 5 credits per image) and returns true if successful.
     */
    suspend fun deductCredits(cost: Int): Boolean = withContext(Dispatchers.IO) {
        val wallet = walletDao.getWallet() ?: return@withContext false
        if (wallet.credits >= cost) {
            walletDao.insertWallet(wallet.copy(credits = wallet.credits - cost))
            true
        } else {
            false
        }
    }

    /**
     * Claims the daily credit reward of +20 credits (once every 24 hours).
     * Returns a pair of (Success, RemainingMillisUntilNextClaim)
     */
    suspend fun claimDailyReward(): Pair<Boolean, Long> = withContext(Dispatchers.IO) {
        val wallet = walletDao.getWallet() ?: return@withContext Pair(false, 0L)
        val currentTime = System.currentTimeMillis()
        val oneDayMillis = 24 * 60 * 60 * 1000L
        val timePassed = currentTime - wallet.lastClaimedAt

        if (timePassed >= oneDayMillis) {
            val updatedWallet = wallet.copy(
                credits = wallet.credits + 20,
                lastClaimedAt = currentTime
            )
            walletDao.insertWallet(updatedWallet)
            Pair(true, oneDayMillis)
        } else {
            val remaining = oneDayMillis - timePassed
            Pair(false, remaining)
        }
    }

    /**
     * Calls Gemini to enhance the user's prompt. If the API key is missing, invalid, or
     * the call fails, it falls back to a high-quality local rule-based enhancer.
     */
    suspend fun enhancePrompt(prompt: String, styleName: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val hasRealApiKey = apiKey.isNotEmpty() && !apiKey.contains("MY_GEMINI_API_KEY")

        if (hasRealApiKey) {
            try {
                val systemPrompt = "You are an expert AI art prompt designer. Enhance the user's visual idea into a vivid, descriptive, single-paragraph prompt of 35 to 60 words for a generator. Keep it direct. Output only the final enhanced prompt, with absolutely no intro/outro or quotes."
                val userPrompt = "Enhance this concept: '$prompt' in '$styleName' style."

                val request = GenerateContentRequest(
                    contents = listOf(Content(parts = listOf(Part(text = userPrompt)))),
                    systemInstruction = Content(parts = listOf(Part(text = systemPrompt))),
                    generationConfig = com.example.data.api.GenerationConfig(
                        temperature = 0.7f,
                        maxOutputTokens = 150
                    )
                )

                val response = RetrofitClient.service.generateContent(apiKey, request)
                val enhanced = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim()
                if (!enhanced.isNullOrBlank()) {
                    return@withContext enhanced
                }
            } catch (e: Exception) {
                Log.e("ArtRepository", "Gemini enhancement failed, using local fallback: ${e.message}")
            }
        }

        // High quality local rule-based prompt enhancer fallback
        val styleDescriptors = when (styleName.lowercase()) {
            "anime" -> "anime key visual, vibrant pastel colors, detailed line art, clean soft shading, mystical elements, high-resolution Studio Ghibli inspired"
            "cyberpunk" -> "futuristic cyberpunk style, neon ambient glow, towering digital skyscrapers, rain-slicked pavement with colorful reflections, 8k resolution, cinematic atmosphere"
            "watercolor" -> "delicate hand-crafted watercolor paint style, soft wet-on-wet color blending, subtle paint splatters, highly artistic, clean pastel washes"
            "3d render" -> "cute 3D stylized render, smooth materials, vibrant high-contrast colors, dramatic volumetric lighting, Pixar character design, raytraced details"
            "oil painting" -> "textured oil painting, heavy visible brushstrokes, rich warm colors, fine museum art feel, dramatic Rembrandt chiaroscuro lighting, masterpiece"
            "steampunk" -> "retro-futuristic steampunk design, copper gears, brass mechanics, vapor steam venting, sepia and gold accents, intricate industrial craftsmanship"
            "pencil sketch" -> "fine pencil sketch, masterfully hand-drawn, graphite paper texture, detailed cross-hatching, classic shading, elegant monochrome portrait"
            "cosmic space" -> "majestic cosmic scene, swirling vibrant nebulas, sparkling stellar dust, grand galaxies, ethereal dreamlike celestial illumination"
            "ghibli" -> "whimsical, nostalgic hand-painted anime backgrounds, Studio Ghibli aesthetic, soft natural lighting, peaceful cloud-filled skies, vibrant greenery, cozy watercolor textures"
            "art nouveau" -> "ornate decorative curves, elegant fluid lines, Alphonse Mucha inspired portraits, organic forms, stylized floral backgrounds, rich gold leaf highlights, high-fidelity fine art"
            "surrealism" -> " Salvador Dali inspired dreamscapes, melting clocks, surreal floating islands, juxtaposition of unusual objects, hyper-realistic details, otherworldly atmosphere, deep subconscious themes"
            "abstract expressionism" -> "Jackson Pollock style dynamic paint splashes, spontaneous expressive brushstrokes, rich colorful textures, emotional energy, non-representational canvas, high action art"
            "vaporwave" -> "80s retro-futurism aesthetic, pink and purple grid sunsets, marble classical sculptures, VHS glitch effects, nostalgic low-poly cyber landscapes, Microsoft Windows 95 icons"
            "pixel art" -> "detailed 16-bit retro video game graphics, crisp pixels, vibrant color palette, nostalgic RPG background art, perfect alignment, classic arcade visual design"
            "origami" -> "intricately folded paper crafts, clean paper folding lines, soft studio shadow and lighting, elegant geometric shapes, minimal textured craft paper aesthetic"
            "impressionism" -> "Claude Monet inspired waterlilies gardens, dappled sunlight filtering through trees, quick visible brush dabs, rich natural colors, shimmering atmospheric lighting"
            "claymation" -> "stop-motion clay animation style, cute cute tactile characters, plasticine models with subtle hand-molded fingerprints, soft studio lighting, playful 3D textures"
            "pop art" -> "Andy Warhol retro halftone dot screens, bold high-contrast color blocks, comic book style thick black outlines, vintage screenprint look, popular culture motifs"
            else -> "beautiful, highly detailed artistic render, gorgeous lighting, 8k resolution, crisp focus"
        }

        "A high-quality representation of $prompt, crafted in a distinct $styleName style. Featuring $styleDescriptors, beautifully composed, clean focus, masterpiece details."
    }

    /**
     * Generates a fully functional image URL using Pollinations.ai with seed and size settings.
     */
    fun buildGeneratedImageUrl(enhancedPrompt: String): String {
        val encodedPrompt = try {
            URLEncoder.encode(enhancedPrompt, "UTF-8")
        } catch (e: Exception) {
            enhancedPrompt.replace(" ", "%20")
        }
        val randomSeed = (1..10000).random()
        return "https://image.pollinations.ai/p/$encodedPrompt?width=1024&height=1024&nologo=true&seed=$randomSeed"
    }

    /**
     * Inserts a newly generated creation.
     */
    suspend fun saveCreation(creation: Creation): Long = withContext(Dispatchers.IO) {
        creationDao.insertCreation(creation)
    }

    /**
     * Deletes a creation.
     */
    suspend fun deleteCreation(creationId: Int) = withContext(Dispatchers.IO) {
        creationDao.deleteCreationById(creationId)
    }

    /**
     * Shares a creation on the community feed.
     */
    suspend fun shareCreation(creationId: Int) = withContext(Dispatchers.IO) {
        val creation = creationDao.getCreationById(creationId)
        if (creation != null) {
            creationDao.updateCreation(creation.copy(isShared = true))
        }
    }

    /**
     * Likes/Unlikes a creation.
     */
    suspend fun toggleLikeCreation(creationId: Int) = withContext(Dispatchers.IO) {
        val creation = creationDao.getCreationById(creationId)
        if (creation != null) {
            val isLiked = creation.isLikedByMe
            val updated = creation.copy(
                isLikedByMe = !isLiked,
                likes = if (isLiked) (creation.likes - 1).coerceAtLeast(0) else creation.likes + 1
            )
            creationDao.updateCreation(updated)
        }
    }

    /**
     * Adds a comment to a creation.
     */
    suspend fun addComment(creationId: Int, text: String, authorName: String = "You") = withContext(Dispatchers.IO) {
        val comment = Comment(
            creationId = creationId,
            authorName = authorName,
            text = text,
            timestamp = System.currentTimeMillis()
        )
        commentDao.insertComment(comment)
    }

    /**
     * Creates a new personal collection.
     */
    suspend fun createCollection(name: String, description: String = ""): Long = withContext(Dispatchers.IO) {
        collectionDao.insertCollection(CollectionEntity(name = name, description = description))
    }

    /**
     * Deletes a collection.
     */
    suspend fun deleteCollection(id: Int) = withContext(Dispatchers.IO) {
        collectionDao.deleteCollectionById(id)
    }

    /**
     * Adds an art creation to a collection.
     */
    suspend fun addCreationToCollection(collectionId: Int, creationId: Int) = withContext(Dispatchers.IO) {
        collectionDao.insertCollectionCreation(CollectionCreationCrossRef(collectionId, creationId))
    }

    /**
     * Removes an art creation from a collection.
     */
    suspend fun removeCreationFromCollection(collectionId: Int, creationId: Int) = withContext(Dispatchers.IO) {
        collectionDao.deleteCollectionCreation(collectionId, creationId)
    }

    /**
     * Checks if a creation is inside a collection.
     */
    suspend fun isCreationInCollection(collectionId: Int, creationId: Int): Boolean = withContext(Dispatchers.IO) {
        collectionDao.isCreationInCollection(collectionId, creationId)
    }

    /**
     * Returns a Flow list of creations in a collection.
     */
    fun getCreationsInCollection(collectionId: Int): Flow<List<Creation>> {
        return collectionDao.getCreationsInCollection(collectionId)
    }

    /**
     * Pre-populates the database with beautiful, shared community images
     * to keep the user experience interactive and colorful immediately on first launch.
     */
    private suspend fun prepopulateDatabaseIfEmpty() {
        val shared = creationDao.getSharedCreations().firstOrNull()
        if (!shared.isNullOrEmpty()) return

        // Insert wallet if it was somehow missing
        initWallet()

        val defaultCreations = listOf(
            Creation(
                id = 101,
                prompt = "Chibi dragon resting on a floating cloud",
                enhancedPrompt = "A cozy chibi pastel-colored baby dragon sleeping soundly on top of a fluffy floating cloud, soft watercolor paint blend, cute round features, whimsical and magical aesthetic",
                styleName = "Watercolor",
                imageUrl = "https://image.pollinations.ai/p/A%20cozy%20chibi%20pastel-colored%20baby%20dragon%20sleeping%20soundly%20on%20top%20of%20a%20fluffy%20floating%20cloud,%20soft%20watercolor%20paint%20blend,%20cute%20round%20features,%20whimsical%20and%20magical%20aesthetic?width=600&height=600&nologo=true&seed=42",
                isShared = true,
                likes = 124,
                isLikedByMe = false,
                authorName = "LunaArt",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 15 // 15 mins ago
            ),
            Creation(
                id = 102,
                prompt = "Cyberpunk street in rain",
                enhancedPrompt = "A neon-lit cyberpunk alleyway in Neo-Tokyo during a soft rain, highly detailed reflections on the wet asphalt, glowing holographic signs, futuristic motorcycle in the background",
                styleName = "Cyberpunk",
                imageUrl = "https://image.pollinations.ai/p/A%20neon-lit%20cyberpunk%20alleyway%20in%20Neo-Tokyo%20during%20a%20soft%20rain,%20highly%20detailed%20reflections%20on%20the%20wet%20asphalt,%20glowing%20holographic%20signs,%20futuristic%20motorcycle%20in%20the%20background?width=600&height=600&nologo=true&seed=99",
                isShared = true,
                likes = 89,
                isLikedByMe = false,
                authorName = "NeonDreamer",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 2 // 2 hours ago
            ),
            Creation(
                id = 103,
                prompt = "Cute fluffy round orange monster",
                enhancedPrompt = "A cute, fluffy, completely round orange little monster sitting on a dark green velvet armchair, giant expressive circular eyes, Pixar styled 3D render, volumetric lighting",
                styleName = "3D Render",
                imageUrl = "https://image.pollinations.ai/p/A%20cute,%20fluffy,%20completely%20round%20orange%20little%20monster%20sitting%20on%20a%20dark%20green%20velvet%20armchair,%20giant%20expressive%20circular%20eyes,%20Pixar%20styled%203D%20render,%20volumetric%20lighting?width=600&height=600&nologo=true&seed=12",
                isShared = true,
                likes = 156,
                isLikedByMe = false,
                authorName = "PixelWizard",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 5 // 5 hours ago
            ),
            Creation(
                id = 104,
                prompt = "Cosmic nebula with space traveller",
                enhancedPrompt = "A breathtaking galactic nebula in deep space with vibrant magenta and dark violet gases, sparkling cosmic stardust, a solitary small astronaut floating peacefully in awe",
                styleName = "Cosmic Space",
                imageUrl = "https://image.pollinations.ai/p/A%20breathtaking%20galactic%20nebula%20in%20deep%20space%20with%20vibrant%20magenta%20and%20dark%20violet%20gases,%20sparkling%20cosmic%20stardust,%20a%20solitary%20small%20astronaut%20floating%20peacefully%20in%20awe?width=600&height=600&nologo=true&seed=88",
                isShared = true,
                likes = 210,
                isLikedByMe = false,
                authorName = "StarryNight",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 24 // 1 day ago
            ),
            Creation(
                id = 105,
                prompt = "Steampunk pocketwatch city",
                enhancedPrompt = "A micro steampunk city constructed entirely inside a golden pocket watch, rotating brass gear bridges, copper chimneys releasing wisps of steam, intricate miniature design",
                styleName = "Steampunk",
                imageUrl = "https://image.pollinations.ai/p/A%20micro%20steampunk%20city%20constructed%20entirely%20inside%20a%20golden%20pocket%20watch,%20rotating%20brass%20gear%20bridges,%20copper%20chimneys%20releasing%20wisps%20of%20steam,%20intricate%20miniature%20design?width=600&height=600&nologo=true&seed=67",
                isShared = true,
                likes = 72,
                isLikedByMe = false,
                authorName = "AstroCoder",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 36 // 1.5 days ago
            )
        )

        defaultCreations.forEach { creation ->
            creationDao.insertCreation(creation)
        }

        // Add some pre-populated comments
        val defaultComments = listOf(
            Comment(creationId = 101, authorName = "Pixie", text = "Omg, this baby dragon is so incredibly adorable! I want a plushie of it! 💕"),
            Comment(creationId = 101, authorName = "ArtLover", text = "The watercolor blends here are amazingly smooth. Splendid choice!"),
            Comment(creationId = 102, authorName = "BladeRunnerX", text = "Now this is real cyberpunk, look at those asphalt wet reflections! 🔥"),
            Comment(creationId = 102, authorName = "Samurai", text = "Which prompt did you write? The holographic glow is outstanding."),
            Comment(creationId = 103, authorName = "ToyCollector", text = "It's so fluffy! It looks like a real physical toy, great 3D depth."),
            Comment(creationId = 104, authorName = "Cosmo", text = "Pure cosmic bliss. This is my new wallpaper, thank you!"),
            Comment(creationId = 105, authorName = "Clockmaker", text = "The attention to gears and steampunk aesthetics is top-tier.")
        )

        defaultComments.forEach { comment ->
            commentDao.insertComment(comment)
        }
    }
}
