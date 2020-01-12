package com.benjaminledet.pokedex.ui.pokemon.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
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

            val firstType = pokemon?.detail?.types?.get(0)
            // Recherche de la couleur compatible avec le type pour l'afficher en backgroundColor
            // Couleurs issus du site https://pokemon.fandom.com/wiki/Types en utilisant l'inspecteur d'élément
            // On considère que le deuxième type, si le pokémon en possède 2, est le type principal
            val backgroundFirstType = switchToSetBackgroundColor(firstType)
            type2.text = getString(R.string.pokemon_type, pokemon?.detail?.types?.get(0).toString().capitalize())
            cardTypes2.setCardBackgroundColor(requireContext().getColor(backgroundFirstType))
            cardTypes.isVisible = true
            if(pokemon?.detail?.types?.size == 2) {
                val secondType = pokemon?.detail?.types?.get(1)
                val backgroundSecondType = switchToSetBackgroundColor(secondType)
                content.setBackgroundColor(requireContext().getColor(switchToSetBackgroundColor(secondType, 1)))
                cardTypes.setCardBackgroundColor(requireContext().getColor(backgroundSecondType))
                type.text = getString(R.string.pokemon_type, pokemon?.detail?.types?.get(1).toString().capitalize())
                cardHeight.setCardBackgroundColor(requireContext().getColor(backgroundSecondType))
                cardWeight.setCardBackgroundColor(requireContext().getColor(backgroundSecondType))
                movesRecyclerFragment.setBackgroundColor(requireContext().getColor(backgroundSecondType))
                cardInfoMovesFragment.setCardBackgroundColor(requireContext().getColor(backgroundSecondType))
            } else {
                content.setBackgroundColor(requireContext().getColor(switchToSetBackgroundColor(firstType, 1)))
                cardHeight.setCardBackgroundColor(requireContext().getColor(backgroundFirstType))
                cardWeight.setCardBackgroundColor(requireContext().getColor(backgroundFirstType))
                movesRecyclerFragment.setBackgroundColor(requireContext().getColor(backgroundFirstType))
                cardInfoMovesFragment.setCardBackgroundColor(requireContext().getColor(backgroundFirstType))
                cardTypes.isVisible = false
            }

            Picasso.get().load(pokemon?.iconUrl).into(icon)
        })

        viewModel.moves.observe(this, Observer { moves ->
            movesRecyclerFragment.adapter = MyAdapter(moves) {

            }
            movesRecyclerFragment.layoutManager = LinearLayoutManager(requireContext())

        })

    }


}