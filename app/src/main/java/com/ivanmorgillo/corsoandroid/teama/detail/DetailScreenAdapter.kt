package com.ivanmorgillo.corsoandroid.teama.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.tabs.TabLayout
import com.ivanmorgillo.corsoandroid.teama.databinding.DetailIngredientInstructionBinding
import com.ivanmorgillo.corsoandroid.teama.databinding.DetailScreenTitleBinding
import com.ivanmorgillo.corsoandroid.teama.databinding.DetailScreenVideoBinding
import com.ivanmorgillo.corsoandroid.teama.databinding.TabButtonDetailsBinding
import com.ivanmorgillo.corsoandroid.teama.detail.DetailScreenViewHolder.IngredientInstructionListViewHolder
import com.ivanmorgillo.corsoandroid.teama.detail.DetailScreenViewHolder.TabLayoutViewHolder
import com.ivanmorgillo.corsoandroid.teama.detail.DetailScreenViewHolder.TitleViewHolder
import com.ivanmorgillo.corsoandroid.teama.detail.DetailScreenViewHolder.VideoViewHolder
import com.ivanmorgillo.corsoandroid.teama.extension.gone
import com.ivanmorgillo.corsoandroid.teama.extension.visible
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import timber.log.Timber

// gli oggetti dentro questa sealed li stiamo aggiungendo a seconda dell'ordine della nostra schermata
// io seguo un pò anche il discorso di ivan perchè la nostra schermata è diversa
sealed class DetailScreenItems {
    data class Title(val title: String) : DetailScreenItems()
    data class Video(val video: String, val image: String) : DetailScreenItems()
    data class IngredientsInstructionsList(
        val ingredients: List<IngredientUI>,
        val instruction: String,
        val isIngredientsVisible: Boolean,
    ) : DetailScreenItems()

    object TabLayout : DetailScreenItems()
}

private const val VIDEO_VIEWTYPE = 1
private const val IGNREDIENTSINSTRUCTIONS_VIEWTYPE = 2
private const val TITLE_VIEWTYPE = 3
private const val TABLAYOUT_VIEWTYPE = 4
private const val YOUTUBE_INDEX = 8

class DetailScreenAdapter(private val onIngredientsClick: () -> Unit, private val onInstructionsClick: () -> Unit) :
    RecyclerView.Adapter<DetailScreenViewHolder>() {
    var items: List<DetailScreenItems> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    /**In base all'elemento che stiamo utilizzando ci ritorna un intero
     * che rappresenta il viewType.
     * */
    override fun getItemViewType(position: Int): Int {
        val item = items[position]
        return when (item) {
            is DetailScreenItems.Video -> VIDEO_VIEWTYPE
            is DetailScreenItems.IngredientsInstructionsList -> IGNREDIENTSINSTRUCTIONS_VIEWTYPE
            is DetailScreenItems.Title -> TITLE_VIEWTYPE
            is DetailScreenItems.TabLayout -> TABLAYOUT_VIEWTYPE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailScreenViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIDEO_VIEWTYPE -> {
                val binding = DetailScreenVideoBinding.inflate(layoutInflater, parent, false)
                VideoViewHolder(binding)
            }
            IGNREDIENTSINSTRUCTIONS_VIEWTYPE -> {
                val binding = DetailIngredientInstructionBinding.inflate(layoutInflater, parent, false)
                IngredientInstructionListViewHolder(binding)
            }
            TITLE_VIEWTYPE -> {
                val binding = DetailScreenTitleBinding.inflate(layoutInflater, parent, false)
                TitleViewHolder(binding)
            }
            TABLAYOUT_VIEWTYPE -> {
                val binding = TabButtonDetailsBinding.inflate(layoutInflater, parent, false)
                TabLayoutViewHolder(binding)
            }
            else -> error("ViewTypeNotValid!")
        }
    }

    override fun onBindViewHolder(holder: DetailScreenViewHolder, position: Int) {
        when (holder) {
            is VideoViewHolder -> holder.bind(items[position] as DetailScreenItems.Video)
            is IngredientInstructionListViewHolder -> holder.bind(
                items[position] as DetailScreenItems.IngredientsInstructionsList
            )
            is TitleViewHolder -> holder.bind(items[position] as DetailScreenItems.Title)
            is TabLayoutViewHolder -> holder.bind(onIngredientsClick, onInstructionsClick)
        }
    }

    override fun getItemCount(): Int = items.size
}

sealed class DetailScreenViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    // view holder per il titolo
    class TitleViewHolder(private val binding: DetailScreenTitleBinding) : DetailScreenViewHolder(binding.root) {
        fun bind(title: DetailScreenItems.Title) {
            binding.detailScreenTitle.text = title.title
        }
    }

    class VideoViewHolder(private val binding: DetailScreenVideoBinding) : DetailScreenViewHolder(binding.root) {
        private var startSeconds = 0f // secondi a cui far iniziare il video (0 = dall'inizio)
        private var videoNotWorking = false
        fun bind(video: DetailScreenItems.Video) {
            if (video.video.isEmpty() || videoNotWorking) { // se il video è vuoto (non esiste) mostra l'immagine
               showImage(video.image)
            } else { // altrimenti nasconde l'immagine e mostra il video
                binding.detailScreenVideo.visible()
                binding.detailScreenImage.gone()
                binding.detailScreenVideo.enableBackgroundPlayback(false)
                binding.detailScreenVideo.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        // esempio video URL: https://www.youtube.com/watch?v=SQnr4Z-7rok
                        val videoId = video.video.substring(video.video.indexOf("watch?v=") + YOUTUBE_INDEX)
                        Timber.d("Sto caricando: https://www.youtube.com/watch?v=$videoId")
                        youTubePlayer.loadVideo(videoId, startSeconds)
                        youTubePlayer.pause()
                    }

                    override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
                        super.onError(youTubePlayer, error)
                        showImage(video.image)
                        videoNotWorking = true
                    }
                })
            }
        }

        private fun showImage(image: String) {
            binding.detailScreenImage.load(image)
            binding.detailScreenImage.visible()
            binding.detailScreenVideo.gone()
        }
    }

    class IngredientInstructionListViewHolder(private val binding: DetailIngredientInstructionBinding) :
        DetailScreenViewHolder(binding.root) {
        fun bind(item: DetailScreenItems.IngredientsInstructionsList) {
            // questa striscia contiene una recyclerview quindi a questa lista serve:
            // - un adapter e una lista di elem da passare all'adapter.
            val adapter = ListIngredientAdapter()
            binding.detailScreenIngredientList.adapter = adapter
            adapter.setIngredients(item.ingredients)
            binding.detailScreenInstruction.text = item.instruction
            if (item.isIngredientsVisible) {
                binding.detailScreenInstruction.gone()
                binding.detailScreenIngredientList.visible()
            } else {
                binding.detailScreenInstruction.visible()
                binding.detailScreenIngredientList.gone()
            }
        }
    }

    class TabLayoutViewHolder(private val binding: TabButtonDetailsBinding) : DetailScreenViewHolder(binding.root) {
        fun bind(onIngredientsClick: () -> Unit, onInstructionsClick: () -> Unit) {
            binding.tabLayoutDetail.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    if (tab.position == 0) {
                        onIngredientsClick()
                    } else {
                        onInstructionsClick()
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) = Unit

                override fun onTabReselected(tab: TabLayout.Tab?) = Unit
            })
        }
    }
}
