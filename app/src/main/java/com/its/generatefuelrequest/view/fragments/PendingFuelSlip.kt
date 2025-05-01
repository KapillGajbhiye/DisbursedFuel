package com.its.generatefuelrequest.view.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.its.generatefuelrequest.databinding.FragmentPendingPodBinding
import com.its.generatefuelrequest.`interface`.TabTitleUpdater
import com.its.generatefuelrequest.model.FuelPumpNameRequest
import com.its.generatefuelrequest.model.FuelSlip
import com.its.generatefuelrequest.network.AppPreferences
import com.its.generatefuelrequest.network.RetrofitClient
import com.its.generatefuelrequest.repositories.Repository
import com.its.generatefuelrequest.view.adapters.PendingSlipAdapter
import com.its.generatefuelrequest.view.dialogs.CustomProgressBar
import com.its.generatefuelrequest.viewModel.MyViewModel
import com.its.generatefuelrequest.viewModel.ViewModelFactoryProvider
import java.util.Locale

class PendingFuelSlip : Fragment() {

    private lateinit var viewModel: MyViewModel
    private lateinit var appPreferences: AppPreferences
    private lateinit var customProgressBar: CustomProgressBar
    private lateinit var adapter: PendingSlipAdapter
    private lateinit var binding: FragmentPendingPodBinding
    private var filterList: ArrayList<FuelSlip> = ArrayList()
    private var fuelSlipList: List<FuelSlip> = ArrayList()
    private var tabTitleUpdater: TabTitleUpdater? = null
    private var tabPosition: Int = 0

    companion object {
        fun newInstance(position: Int, tabTitleUpdater: TabTitleUpdater): PendingFuelSlip {
            val fragment = PendingFuelSlip()
            fragment.tabPosition = position
            fragment.tabTitleUpdater = tabTitleUpdater
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPendingPodBinding.inflate(inflater, container, false)
        initialize()
        fetchFuelSlipData(isSwipeRefresh = false)
        setupSearchView()
        setupSwipeRefresh() // Setup swipe refresh
        return binding.root
    }

    private fun initialize() {
        appPreferences = AppPreferences(requireContext())
        customProgressBar = CustomProgressBar(requireContext()).apply {
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            setCancelable(false)
        }

        adapter = PendingSlipAdapter(requireContext(), filterList)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        val apiService = RetrofitClient.createApiService()
        val repository = Repository(apiService)
        val factory = ViewModelFactoryProvider(repository)
        viewModel = ViewModelProvider(this, factory)[MyViewModel::class.java]
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            fetchFuelSlipData(isSwipeRefresh = true)  // Fetch data when swiped
        }
    }

    private fun fetchFuelSlipData(isSwipeRefresh: Boolean = false) {
        if (!isSwipeRefresh) {
            customProgressBar.show() // Show progress bar only for the initial load
        }

        val authToken = appPreferences.getUserData().authToken
        val fuelPump = FuelPumpNameRequest(appPreferences.getUserData().fuelPumpCompanyName, false)
        Log.d("req", "fetchFuelSlipData: $fuelPump, $authToken")

        viewModel.fetchFuelSlipData(fuelPump, authToken, customProgressBar)
        viewModel.fuelSlipDataResponse.observe(viewLifecycleOwner) { response ->
            binding.swipeRefreshLayout.isRefreshing = false // Stop refreshing animation
            customProgressBar.dismiss() // Dismiss progress bar regardless of refresh type

            if (response != null && response.success) {
                fuelSlipList = response.data
                updateUI(fuelSlipList)
            } else {
                handleError(response?.message)
            }
        }
    }

    private fun updateUI(fuelSlipList: List<FuelSlip>) {
        filterList.clear()
        filterList.addAll(fuelSlipList)
        adapter.notifyDataSetChanged()
        tabTitleUpdater?.updateTabTitle(tabPosition, filterList.size)

        if (filterList.isEmpty()) {
            binding.noDataCard.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        } else {
            binding.noDataCard.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
    }

    private fun handleError(message: String?) {
        Toast.makeText(requireContext(), message ?: "Unknown Error", Toast.LENGTH_SHORT).show()
        binding.noDataCard.visibility = View.VISIBLE
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList.clear()
                if (newText.isNullOrEmpty()) {
                    filterList.addAll(fuelSlipList)
                } else {
                    val query = newText.lowercase(Locale.getDefault())
                    filterList.addAll(fuelSlipList.filter {
                        it.vehicleNo.lowercase(Locale.getDefault()).contains(query)
                    })
                }
                updateAdapter()
                return true
            }
        })
    }

    private fun updateAdapter() {
        adapter.updateData(filterList)
        tabTitleUpdater?.updateTabTitle(tabPosition, filterList.size)
    }

}
