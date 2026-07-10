package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.Comment
import com.example.data.model.Creation
import com.example.data.model.UserWallet
import com.example.data.model.CollectionEntity
import com.example.data.repository.ArtRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

data class ArtStyle(
    val name: String,
    val description: String,
    val thumbnail: String
)

class MainViewModel(private val repository: ArtRepository) : ViewModel() {

    companion object {
        private const val CREDIT_COST_PER_IMAGE = 5
        private const val UPDATE_INTERVAL_MILLIS = 10_000L
        private const val AUTHOR_NAME = "You"
        private const val ONE_DAY_MILLIS = 24 * 60 * 60 * 1000L
        private const val HOURS_PER_DAY = 24
        private const val MILLIS_PER_HOUR = 1000 * 60 * 60
        private const val MILLIS_PER_MINUTE = 1000 * 60
    }

    val wallet: StateFlow<UserWallet?> = repository.walletFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allCreations: StateFlow<List<Creation>> = repository.allCreations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val myHistory: StateFlow<List<Creation>> = repository.allCreations
        .map { list -> list.filter { it.authorName == AUTHOR_NAME } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sharedCreations: StateFlow<List<Creation>> = repository.sharedCreations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCollections: StateFlow<List<CollectionEntity>> = repository.allCollections
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedCollection = MutableStateFlow<CollectionEntity?>(null)
    val selectedCollection = _selectedCollection.asStateFlow()

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val creationsInSelectedCollection: StateFlow<List<Creation>> = _selectedCollection
        .flatMapLatest { collection ->
            if (collection != null) {
                repository.getCreationsInCollection(collection.id)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val styles = listOf(
        ArtStyle("Anime", "Cute Chibi & Japanese anime art key visuals", "https://image.pollinations.ai/p/beautiful%20anime%20key%20visual%20glowing%20pastel?width=150&height=150&nologo=true&seed=42"),
        ArtStyle("Cyberpunk", "Neon-lit cities and high-tech retro aesthetics", "https://image.pollinations.ai/p/cyberpunk%20metropolis%20neon%20reflections?width=150&height=150&nologo=true&seed=99"),
        ArtStyle("Watercolor", "Soft, elegant paper washes and wet blending", "https://image.pollinations.ai/p/watercolor%20painting%20nature%20forest?width=150&height=150&nologo=true&seed=55"),
        ArtStyle("3D Render", "Smooth stylized 3D toy and Pixar character look", "https://image.pollinations.ai/p/cute%203D%20toy%20character%20volumetric%20lighting?width=150&height=150&nologo=true&seed=12"),
        ArtStyle("Oil Painting", "Vivid heavy brushstrokes and classic fine art textures", "https://image.pollinations.ai/p/van%20gogh%20oil%20painting%20style%20landscape?width=150&height=150&nologo=true&seed=23"),
        ArtStyle("Steampunk", "Copper, golden clockwork gears and vintage machinery", "https://image.pollinations.ai/p/steampunk%20industrial%20gears%20gold?width=150&height=150&nologo=true&seed=67"),
        ArtStyle("Pencil Sketch", "Handcrafted monochrome charcoal and cross-hatch shading", "https://image.pollinations.ai/p/detailed%20pencil%20sketch%20classic%20portrait?width=150&height=150&nologo=true&seed=10"),
        ArtStyle("Cosmic Space", "Deep dark violet nebulae, stardust, and celestial events", "https://image.pollinations.ai/p/cosmic%20space%20purple%20nebula?width=150&height=150&nologo=true&seed=88"),
        ArtStyle("Ghibli", "Whimsical, nostalgic painted backgrounds with soft hand-drawn anime look", "https://image.pollinations.ai/p/ghibli%20style%20peaceful%20nature%20meadow%20clouds?width=150&height=150&nologo=true&seed=150"),
        ArtStyle("Art Nouveau", "Ornate decorative curves, elegant natural forms, and flowing gold outlines", "https://image.pollinations.ai/p/art%20nouveau%20elegant%20portrait%20gold%20leaves?width=150&height=150&nologo=true&seed=121"),
        ArtStyle("Surrealism", "Dreamlike irrational scenes, floating paradoxes, and Dali-inspired clockwork", "https://image.pollinations.ai/p/surrealism%20dream%20landscape%20melting%20clocks%20floating%20islands?width=150&height=150&nologo=true&seed=302"),
        ArtStyle("Abstract Expressionism", "Spontaneous paint splatters, dynamic brushwork, and high emotional energy", "https://image.pollinations.ai/p/abstract%20expressionism%20colorful%20paint%20splatter%20canvas?width=150&height=150&nologo=true&seed=403"),
        ArtStyle("Vaporwave", "Retro-futuristic pink-purple neon grid with classical marble statues", "https://image.pollinations.ai/p/vaporwave%20aesthetic%20neon%20grid%20pink%20sunset%20statue?width=150&height=150&nologo=true&seed=704"),
        ArtStyle("Pixel Art", "Retro 16-bit arcade video game sprites and vibrant pixelated details", "https://image.pollinations.ai/p/retro%20pixel%20art%20fantasy%20castle%20landscape?width=150&height=150&nologo=true&seed=805"),
        ArtStyle("Origami", "Intricately folded clean paper sculptures with soft studio lighting", "https://image.pollinations.ai/p/origami%20paper%20crane%20sculpture%20soft%20colors?width=150&height=150&nologo=true&seed=906"),
        ArtStyle("Impressionism", "Dappled outdoor lighting, visible dynamic paint dabs, and waterlily palettes", "https://image.pollinations.ai/p/impressionist%20oil%20painting%20garden%20waterlilies%20sunlight?width=150&height=150&nologo=true&seed=107"),
        ArtStyle("Claymation", "Stop-motion plasticine clay model look with tactile fingerprint textures", "https://image.pollinations.ai/p/cute%20tactile%20claymation%20character%20plasticine%20toy?width=150&height=150&nologo=true&seed=208"),
        ArtStyle("Pop Art", "Bold retro halftone comic book dots with vibrant high-contrast screen print", "https://image.pollinations.ai/p/retro%20pop%20art%20halftone%20comic%20book%20style?width=150&height=150&nologo=true&seed=309")
    )

    // Generation States
    private val _prompt = MutableStateFlow("")
    val prompt = _prompt.asStateFlow()

    private val _selectedStyle = MutableStateFlow(styles[0])
    val selectedStyle = _selectedStyle.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating = _isGenerating.asStateFlow()

    private val _generationError = MutableStateFlow<String?>(null)
    val generationError = _generationError.asStateFlow()

    private val _recentlyGenerated = MutableStateFlow<Creation?>(null)
    val recentlyGenerated = _recentlyGenerated.asStateFlow()

    // Rewards States
    private val _rewardStatus = MutableStateFlow<String?>(null)
    val rewardStatus = _rewardStatus.asStateFlow()

    private val _remainingClaimMillis = MutableStateFlow(0L)
    val remainingClaimMillis = _remainingClaimMillis.asStateFlow()

    // Comments States
    private val _activeCommentPostId = MutableStateFlow<Int?>(null)
    val activeCommentPostId = _activeCommentPostId.asStateFlow()

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val activeComments: StateFlow<List<Comment>> = _activeCommentPostId
        .flatMapLatest { id ->
            if (id != null) repository.getCommentsForCreation(id)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private var countdownJob: Job? = null

    init {
        startClaimCountdownTicker()
    }

    fun onPromptChanged(newPrompt: String) {
        _prompt.value = newPrompt
    }

    fun enhancePrompt(currentPrompt: String, styleName: String, onEnhanced: (String) -> Unit) {
        viewModelScope.launch {
            val result = repository.enhancePrompt(currentPrompt, styleName)
            onEnhanced(result)
        }
    }

    fun onStyleSelected(style: ArtStyle) {
        _selectedStyle.value = style
    }

    fun dismissError() {
        _generationError.value = null
    }

    fun clearRecentlyGenerated() {
        _recentlyGenerated.value = null
    }

    fun generateImage() {
        val userPrompt = _prompt.value.trim()
        if (userPrompt.isEmpty()) {
            _generationError.value = "Please enter a creative prompt first."
            return
        }

        _isGenerating.value = true
        _generationError.value = null
        _recentlyGenerated.value = null

        viewModelScope.launch {
            if (!repository.deductCredits()) {
                _generationError.value = "Insufficient credits! Claim daily rewards or generate later."
                _isGenerating.value = false
                return@launch
            }

            try {
                val enhanced = repository.enhancePrompt(userPrompt, _selectedStyle.value.name)
                val imageUrl = repository.buildGeneratedImageUrl(enhanced)

                val creation = Creation(
                    prompt = userPrompt,
                    enhancedPrompt = enhanced,
                    styleName = _selectedStyle.value.name,
                    imageUrl = imageUrl,
                    isShared = false,
                    likes = 0,
                    isLikedByMe = false,
                    authorName = AUTHOR_NAME
                )

                val id = repository.saveCreation(creation)
                _recentlyGenerated.value = creation.copy(id = id.toInt())
            } catch (e: Exception) {
                _generationError.value = "Generation failed: ${e.localizedMessage ?: "Unknown error"}"
            } finally {
                _isGenerating.value = false
            }
        }
    }

    /**
     * Deletes a user creation.
     */
    fun deleteCreation(id: Int) {
        viewModelScope.launch {
            repository.deleteCreation(id)
        }
    }

    /**
     * Shares a creation to the community feed.
     */
    fun shareCreationToCommunity(id: Int) {
        viewModelScope.launch {
            repository.shareCreation(id)
            _recentlyGenerated.value = _recentlyGenerated.value?.let {
                if (it.id == id) it.copy(isShared = true) else it
            }
        }
    }

    /**
     * Toggles liking a post.
     */
    fun toggleLike(postId: Int) {
        viewModelScope.launch {
            repository.toggleLikeCreation(postId)
        }
    }

    /**
     * Sets which post is active for comments view.
     */
    fun openCommentsForPost(postId: Int?) {
        _activeCommentPostId.value = postId
    }

    fun submitComment(postId: Int, text: String) {
        val commentText = text.trim()
        if (commentText.isEmpty()) return

        viewModelScope.launch {
            repository.addComment(postId, commentText, AUTHOR_NAME)
        }
    }

    fun claimDailyCredits() {
        viewModelScope.launch {
            val result = repository.claimDailyReward()
            if (result.first) {
                _rewardStatus.value = "Claimed successfully! +20 Credits awarded! 🎉"
                startClaimCountdownTicker()
            } else {
                val hours = result.second / MILLIS_PER_HOUR
                val mins = (result.second % MILLIS_PER_HOUR) / MILLIS_PER_MINUTE
                _rewardStatus.value = "Already claimed! Try again in $hours hrs $mins mins."
            }
        }
    }

    fun dismissRewardStatus() {
        _rewardStatus.value = null
    }

    private fun startClaimCountdownTicker() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            while (true) {
                val currentWallet = repository.walletFlow.firstOrNull() ?: wallet.value
                if (currentWallet != null) {
                    val timePassed = System.currentTimeMillis() - currentWallet.lastClaimedAt
                    val remaining = (ONE_DAY_MILLIS - timePassed).coerceAtLeast(0L)
                    _remainingClaimMillis.value = remaining
                }
                delay(UPDATE_INTERVAL_MILLIS)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }

    /**
     * Create a new custom collection.
     */
    fun createCollection(name: String, description: String = "") {
        if (name.trim().isEmpty()) return
        viewModelScope.launch {
            repository.createCollection(name.trim(), description.trim())
        }
    }

    /**
     * Delete an entire collection.
     */
    fun deleteCollection(collectionId: Int) {
        viewModelScope.launch {
            if (_selectedCollection.value?.id == collectionId) {
                _selectedCollection.value = null
            }
            repository.deleteCollection(collectionId)
        }
    }

    /**
     * Selects or clears the active collection.
     */
    fun selectCollection(collection: CollectionEntity?) {
        _selectedCollection.value = collection
    }

    /**
     * Add a generated image (Creation) to a collection.
     */
    fun addCreationToCollection(collectionId: Int, creationId: Int) {
        viewModelScope.launch {
            repository.addCreationToCollection(collectionId, creationId)
        }
    }

    /**
     * Remove a creation from a collection.
     */
    fun removeCreationFromCollection(collectionId: Int, creationId: Int) {
        viewModelScope.launch {
            repository.removeCreationFromCollection(collectionId, creationId)
        }
    }
}

class MainViewModelFactory(private val repository: ArtRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
