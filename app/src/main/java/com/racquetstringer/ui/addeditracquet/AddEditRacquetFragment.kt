package com.racquetstringer.ui.addeditracquet

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.racquetstringer.ui.MainActivity
import com.racquetstringer.racquetstringer.R

class AddEditRacquetFragment : Fragment() {

    companion object {
        fun newInstance() = AddEditRacquetFragment()
    }

    private lateinit var viewModel: AddEditRacquetViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.add_edit_racquet_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AddEditRacquetViewModel::class.java)

        val racquetId = activity?.intent?.getStringExtra(MainActivity.RACQUET_ID)

        if (racquetId != null) {
            viewModel.getRacquet(racquetId)
        }

    }

}
