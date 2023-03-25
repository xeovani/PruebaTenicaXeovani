package com.example.pruebatecnicaxeovani.view.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pruebatecnicaxeovani.R
import com.example.pruebatecnicaxeovani.databinding.FragmentHomeBinding
import com.example.pruebatecnicaxeovani.responses2.KnownFor
import com.example.pruebatecnicaxeovani.view.adapters.AdapterPopularPerson

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var viewModel: HomeViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdapterPopularPerson
    private lateinit var popularPersonMovie: List<KnownFor>


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        viewModel.getPerson(this.requireContext().applicationContext)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val layoutManager = LinearLayoutManager(context)
        recyclerView = root.findViewById(R.id.recyclerMoviePopularPersona)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
/*        adapter = AdapterPopularPerson(popularPersonMovie)
        recyclerView.adapter = adapter*/

        viewModel.personLiveData.observe(viewLifecycleOwner, Observer { it ->
            adapter = AdapterPopularPerson(it.knownFor)
            recyclerView.adapter = adapter

        })


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}