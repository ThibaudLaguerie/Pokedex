package com.benjaminledet.pokedex.ui.pokemon.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.benjaminledet.pokedex.R
import com.benjaminledet.pokedex.data.model.Pokemon
import com.benjaminledet.pokedex.data.repository.utils.Status
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_pokemon_detail.*
import kotlinx.android.synthetic.main.fragment_pokemon_detail.*
import kotlinx.android.synthetic.main.fragment_pokemon_detail.cardHeight
import kotlinx.android.synthetic.main.fragment_pokemon_detail.cardTypes
import kotlinx.android.synthetic.main.fragment_pokemon_detail.cardTypes2
import kotlinx.android.synthetic.main.fragment_pokemon_detail.cardWeight
import kotlinx.android.synthetic.main.fragment_pokemon_detail.content
import kotlinx.android.synthetic.main.fragment_pokemon_detail.height
import kotlinx.android.synthetic.main.fragment_pokemon_detail.icon
import kotlinx.android.synthetic.main.fragment_pokemon_detail.move
import kotlinx.android.synthetic.main.fragment_pokemon_detail.progressBar
import kotlinx.android.synthetic.main.fragment_pokemon_detail.type
import kotlinx.android.synthetic.main.fragment_pokemon_detail.type2
import kotlinx.android.synthetic.main.fragment_pokemon_detail.weight
import org.koin.androidx.viewmodel.ext.android.viewModel

class PokemonDetailFragment: Fragment() {

    private val viewModel by viewModel<PokemonDetailViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pokemon_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.pokemonId.postValue(arguments?.getInt("pokemonId"))

        viewModel.refreshState.observe(viewLifecycleOwner, Observer { refreshState ->
            progressBar.isVisible = refreshState.status == Status.RUNNING
            content.isVisible = refreshState.status != Status.RUNNING
        })

        viewModel.pokemon.observe(viewLifecycleOwner, Observer { pokemon ->
            activity?.title = pokemon?.name
            weight.text = getString(R.string.pokemon_weight, pokemon?.detail?.weight.toString())
            height.text = getString(R.string.pokemon_height, pokemon?.detail?.height.toString())

            // Recherche de la couleur compatible avec le type pour l'afficher en backgroundColor
            // Couleurs issus du site https://pokemon.fandom.com/wiki/Types en utilisant l'inspecteur d'élément
            // On considère que le deuxième type, si le pokémon en possède 2, est le type principal
            val backgroundFirstType = switchToSetBackgroundColor(pokemon, 0)
            type2.text = getString(R.string.pokemon_type, pokemon?.detail?.types?.get(0).toString().capitalize())

            cardTypes2.setCardBackgroundColor(requireContext().getColor(backgroundFirstType))
            cardTypes.isVisible = true
            if(pokemon?.detail?.types?.size == 2) {

                val backgroundSecondType = switchToSetBackgroundColor(pokemon, 1)
                content.setBackgroundColor(requireContext().getColor(switchToSetBackgroundColor(pokemon, 3)))
                cardTypes.setCardBackgroundColor(requireContext().getColor(backgroundSecondType))
                type.text = getString(R.string.pokemon_type, pokemon?.detail?.types?.get(1).toString().capitalize())
                cardHeight.setCardBackgroundColor(requireContext().getColor(backgroundSecondType))
                cardWeight.setCardBackgroundColor(requireContext().getColor(backgroundSecondType))
            } else {
                content.setBackgroundColor(requireContext().getColor(switchToSetBackgroundColor(pokemon, 2)))
                cardHeight.setCardBackgroundColor(requireContext().getColor(backgroundFirstType))
                cardWeight.setCardBackgroundColor(requireContext().getColor(backgroundFirstType))
                cardTypes.isVisible = false
            }
            move.text = getString(R.string.pokemon_move, pokemon?.detail?.moves.toString())

            Picasso.get().load(pokemon?.iconUrl).into(icon)
        })
    }

    /**
     * Permet de choisir la couleur à utiliser selon le type, et selon l'utilisation que l'on souhaite en faire.
     * @param pokemon
     * @param index
     * @return Un entier correspondant à une couleur dans colors.xml
     */
    fun switchToSetBackgroundColor(pokemon: Pokemon?, index: Int): Int {
        // Si l'index est inférieur ou égal à 1, la fonction est utilisé dans le cadre de la couleur de fond du type  correspondant
        if(index <= 1) {
            return when(pokemon?.detail?.types?.get(index)) {
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
            // Si l'index est supérieur à 1, la fonction est utilisé dans le cadre de la couleur de fond de l'écran entier
        } else {
            var i = 0
            // Si l'index est égal à 3, cela signifie que le pokémon a 2 types et on choisit la variante du 2ème type dans ce cas là
            if(index == 3) {
                i = 1
            }
            return when(pokemon?.detail?.types?.get(i)) {
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
}