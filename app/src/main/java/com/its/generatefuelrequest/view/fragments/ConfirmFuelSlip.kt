package com.its.generatefuelrequest.view.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.its.generatefuelrequest.R
import com.its.generatefuelrequest.`interface`.TabTitleUpdater
import com.its.generatefuelrequest.model.FuelPumpNameRequest
import com.its.generatefuelrequest.model.FuelSlip
import com.its.generatefuelrequest.network.AppPreferences
import com.its.generatefuelrequest.network.RetrofitClient
import com.its.generatefuelrequest.repositories.Repository
import com.its.generatefuelrequest.view.adapters.ConfirmSlipAdapter
import com.its.generatefuelrequest.view.dialogs.CustomProgressBar
import com.its.generatefuelrequest.viewModel.MyViewModel
import com.its.generatefuelrequest.viewModel.ViewModelFactoryProvider
import java.util.Locale

class ConfirmFuelSlip : Fragment() {

    private lateinit var viewModel: MyViewModel
    private lateinit var appPreferences: AppPreferences
    private lateinit var customProgressBar: CustomProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var noDataFoundLayout: LinearLayout
    private var tabTitleUpdater: TabTitleUpdater? = null
    private var tabPosition: Int = 1
    private lateinit var searchView: SearchView
    private var filterList: ArrayList<FuelSlip> = ArrayList()
    private lateinit var adapter: ConfirmSlipAdapter

    companion object {
        fun newInstance(position: Int, tabTitleUpdater: TabTitleUpdater): ConfirmFuelSlip {
            val fragment = ConfirmFuelSlip()
            fragment.tabPosition = position
            fragment.tabTitleUpdater = tabTitleUpdater
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_confirm_pod, container, false)

        appPreferences = AppPreferences(requireContext())
        customProgressBar = CustomProgressBar(requireContext())
        customProgressBar.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        customProgressBar.setCancelable(false)
        recyclerView = view.findViewById(R.id.recyclerViewConfirmTab)
        noDataFoundLayout = view.findViewById(R.id.layoutView)
        searchView = view.findViewById(R.id.searchView) // Link the searchView to your layout

        val apiService = RetrofitClient.createApiService()

        val repository = Repository(apiService)
        val authToken = appPreferences.getUserData().authToken
        val fuelPump = FuelPumpNameRequest(appPreferences.getUserData().fuelPumpCompanyName, true)
        Log.d("request", "onCreateView: $fuelPump , $authToken")
        val fuelSlipList = mutableListOf<FuelSlip>()

        customProgressBar.show()
        viewModel = ViewModelProvider(
            this,
            ViewModelFactoryProvider(repository)
        )[MyViewModel::class.java]

        viewModel.fetchFuelSlipData(fuelPump, authToken,customProgressBar)
        viewModel.fuelSlipDataResponse.observe(viewLifecycleOwner) { response ->
            customProgressBar.dismiss()

            if (response != null) {
                if (response.success) {
                    fuelSlipList.clear()
                    response.data.let { fuelSlipList.addAll(it) }
                    tabTitleUpdater?.updateTabTitle(tabPosition, fuelSlipList.size)

                    adapter = ConfirmSlipAdapter(requireContext(), fuelSlipList)
                    recyclerView.layoutManager = LinearLayoutManager(requireContext())
                    recyclerView.adapter = adapter

                    if (fuelSlipList.isEmpty()) {
                        noDataFoundLayout.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                    } else {
                        noDataFoundLayout.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                    }
                } else {
                    Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT)
                        .show()
                    Log.e("ConfirmFuelSlip", "API call unsuccessful: ${response.message}")
                    noDataFoundLayout.visibility = View.VISIBLE
                }
            } else {
                Log.e("ConfirmFuelSlip", "Null response received")
                noDataFoundLayout.visibility = View.VISIBLE
            }
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                filterList.clear()
                if (newText.isNotEmpty()) {
                    val query = newText.lowercase(Locale.getDefault())
                    fuelSlipList.forEach {
                        if (it.vehicleNo.lowercase(Locale.getDefault()).contains(query)) {
                            filterList.add(it)
                        }
                    }
                } else {
                    filterList.addAll(fuelSlipList)
                }
                filterData()
                return true
            }
        })
        return view
    }

    private fun filterData() {
        if (::adapter.isInitialized) {
            adapter.updateData(filterList)
            tabTitleUpdater?.updateTabTitle(tabPosition, filterList.size)
            recyclerView.visibility = if (filterList.isEmpty()) View.GONE else View.VISIBLE
            noDataFoundLayout.visibility = if (filterList.isEmpty()) View.VISIBLE else View.GONE
        } else {
            Log.e("ConfirmFuelSlip", "Adapter is not initialized")
        }
    }

}
