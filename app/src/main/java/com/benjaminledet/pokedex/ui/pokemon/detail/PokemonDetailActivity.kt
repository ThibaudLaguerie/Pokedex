package com.benjaminledet.pokedex.ui.pokemon.detail

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.benjaminledet.pokedex.R
import com.benjaminledet.pokedex.data.repository.utils.Status
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_pokemon_detail.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class PokemonDetailActivity: AppCompatActivity() {

    private val viewModel by viewModel<PokemonDetailViewModel>()

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
            var background = "#FFFFFF"
            if(pokemon?.detail?.types?.get(0).equals("poison")) {
                 background = "#A040A0"
            }
            type.text = getString(R.string.pokemon_type, pokemon?.detail?.types.toString())
            type.setBackgroundColor(Color.parseColor(background))
            move.text = getString(R.string.pokemon_move, pokemon?.detail?.moves.toString())

            Picasso.get().load(pokemon?.iconUrl).into(icon)
        })

        viewModel.moves.observe(this, Observer { moves ->
            Log.d("PokemonDetailActivity", "moves : $moves")
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