package com.benjaminledet.pokedex.ui.pokemon.detail

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.benjaminledet.pokedex.R
import com.benjaminledet.pokedex.data.model.Pokemon
import com.benjaminledet.pokedex.data.model.PokemonDetail
import com.benjaminledet.pokedex.data.repository.utils.Status
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_pokemon_detail.*
import kotlinx.android.synthetic.main.item_pocket.*
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.viewModel

class PokemonDetailActivity: AppCompatActivity() {

    private val viewModel by viewModel<PokemonDetailViewModel>()

    @SuppressLint("ResourceAsColor", "ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pokemon_detail)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel.pokemonId.postValue(intent.getIntExtra("pokemonId", 0))

        viewModel.refreshState.observe(this, Observer { refreshState ->
            progressBar.isVisible = refreshState.status == Status.RUNNING
            content.isVisible = refreshState.status != Status.RUNNING
        })

        viewModel.pokemon.observe(this, Observer { pokemon ->

            title = pokemon?.name
            weight.text = getString(R.string.pokemon_weight, pokemon?.detail?.weight.toString())
            height.text = getString(R.string.pokemon_height, pokemon?.detail?.height.toString())
            val firstType = pokemon?.detail?.types?.get(0)
            // Recherche de la couleur compatible avec le type pour l'afficher en backgroundColor
            // Couleurs issus du site https://pokemon.fandom.com/wiki/Types en utilisant l'inspecteur d'élément
            // On considère que le deuxième type, si le pokémon en possède 2, est le type principal
            val backgroundFirstType = switchToSetBackgroundColor(firstType)
            type2.text = getString(R.string.pokemon_type, pokemon?.detail?.types?.get(0).toString().capitalize())
            cardTypes2.setCardBackgroundColor(this.getColor(backgroundFirstType))
            cardTypes.isVisible = true
            if(pokemon?.detail?.types?.size == 2) {
                val secondType = pokemon?.detail?.types?.get(1)
                val backgroundSecondType = switchToSetBackgroundColor(secondType)
                content.setBackgroundColor(this.getColor(switchToSetBackgroundColor(secondType, 1)))
                cardTypes.setCardBackgroundColor(this.getColor(backgroundSecondType))
                type.text = getString(R.string.pokemon_type, pokemon?.detail?.types?.get(1).toString().capitalize())
                cardHeight.setCardBackgroundColor(this.getColor(backgroundSecondType))
                cardWeight.setCardBackgroundColor(this.getColor(backgroundSecondType))
                movesRecycler.setBackgroundColor(this.getColor(backgroundSecondType))
                cardInfoMoves.setCardBackgroundColor(this.getColor(backgroundSecondType))
            } else {
                content.setBackgroundColor(this.getColor(switchToSetBackgroundColor(firstType, 1)))
                cardHeight.setCardBackgroundColor(this.getColor(backgroundFirstType))
                cardWeight.setCardBackgroundColor(this.getColor(backgroundFirstType))
                movesRecycler.setBackgroundColor(this.getColor(backgroundFirstType))
                cardInfoMoves.setCardBackgroundColor(this.getColor(backgroundFirstType))
                cardTypes.isVisible = false
            }


            Picasso.get().load(pokemon?.iconUrl).into(icon)
        })

        viewModel.moves.observe(this, Observer { moves ->
           movesRecycler.adapter = MyAdapter(moves) {

        }
        movesRecycler.layoutManager = LinearLayoutManager(this)

    })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        else -> true
    }




}

/**
 * Permet de choisir la couleur à utiliser selon le type, et selon l'utilisation que l'on souhaite en faire.
 * @param valType
 * @param id
 * @return Un entier correspondant à une couleur dans colors.xml
 */
fun switchToSetBackgroundColor(valType: String?, id: Int = 0): Int {
    // Si l'index est inférieur ou égal à 1, la fonction est utilisé dans le cadre de la couleur de fond du type  correspondant
    if(id == 0) {
        return when(valType) {
            "normal" -> R.color.normalType
            "fire" -> R.color.fireType
            "water" -> R.color.waterType
            "grass" -> R.color.grassType
            "electric" -> R.color.electricType
            "ice" -> R.color.iceType
            "fighting" -> R.color.fightingType
            "poison" -> R.color.poisonType
            "ground" -> R.color.groundType
            "flying" -> R.color.flyingType
            "psychic" -> R.color.psychicType
            "bug" -> R.color.bugType
            "rock" -> R.color.rockType
            "ghost" -> R.color.ghostType
            "dark" -> R.color.darkType
            "dragon" -> R.color.dragonType
            "steel" -> R.color.steelType
            "fairy" -> R.color.fairyType
            // Des tests effectués ont eu des crash pour certains pokémons en raison d'un problème par rapport à Color.WHITE, décision de le passer en tant que resource dans colors.xml (ex de crash: Caterpie)
            else -> R.color.noTypeError
        }
    } else {
        return when(valType) {
            "normal" -> R.color.normalTypeVariant
            "fire" -> R.color.fireTypeVariant
            "water" -> R.color.waterTypeVariant
            "grass" -> R.color.grassTypeVariant
            "electric" -> R.color.electricTypeVariant
            "ice" -> R.color.iceTypeVariant
            "fighting" -> R.color.fightingTypeVariant
            "poison" -> R.color.poisonTypeVariant
            "ground" -> R.color.groundTypeVariant
            "flying" -> R.color.flyingTypeVariant
            "psychic" -> R.color.psychicTypeVariant
            "bug" -> R.color.bugTypeVariant
            "rock" -> R.color.rockTypeVariant
            "ghost" -> R.color.ghostTypeVariant
            "dark" -> R.color.darkTypeVariant
            "dragon" -> R.color.dragonTypeVariant
            "steel" -> R.color.steelTypeVariant
            "fairy" -> R.color.fairyTypeVariant
            // Des tests effectués ont eu des crash pour certains pokémons en raison d'un problème par rapport à Color.WHITE, décision de le passer en tant que resource dans colors.xml (ex de crash: Caterpie)
            else -> R.color.noTypeError
        }
    }
}