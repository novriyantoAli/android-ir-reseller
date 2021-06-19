package id.co.insinyur.irreseller.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import id.co.insinyur.irreseller.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel

class SettingFragment : BottomSheetDialogFragment() {

    private val viewModel: SettingViewModel by viewModel()

    private lateinit var etHost: EditText

    private lateinit var etPort: EditText

    private lateinit var btnSave: Button

    override fun onCreateView(inflater: LayoutInflater, group: ViewGroup?, state: Bundle?): View? {

        val root = inflater.inflate(R.layout.fragment_setting, group, false)

        etHost = root.findViewById(R.id.et_host)
        etPort = root.findViewById(R.id.et_port)
        btnSave = root.findViewById(R.id.btn_save)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.stateApp.observe(this, {
            if (it != null) {
                etHost.setText(it.url)
                etPort.setText(it.port)
            }
        })

        viewModel.message.observe(this, {
            if (it != null) { Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show() }
        })

        viewModel.success.observe(this, {
            if (it != null && it == true) { this@SettingFragment.dismiss() }
        })

        btnSave.setOnClickListener {
            etHost.error = null
            if (etHost.text.toString().trim().isEmpty()){
                etHost.error = resources.getString(R.string.cannot_empty)
                return@setOnClickListener
            }

            etPort.error = null
            if (etPort.text.toString().trim().isEmpty()) {
                etPort.error = resources.getString(R.string.cannot_empty)
                return@setOnClickListener
            }

            save(etHost.text.toString(), etPort.text.toString())
        }
    }

    private fun save(host: String, port: String) = CoroutineScope(Dispatchers.Main).launch {
        viewModel.save(host, port)
    }
}