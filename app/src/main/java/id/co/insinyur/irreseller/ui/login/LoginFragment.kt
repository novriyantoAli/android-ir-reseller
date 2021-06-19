package id.co.insinyur.irreseller.ui.login

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.lifecycle.Observer
import id.co.insinyur.irreseller.R
import id.co.insinyur.irreseller.base.BaseFragment
import id.co.insinyur.irreseller.model.LoginRequest
import id.co.insinyur.irreseller.ui.setting.SettingFragment
import id.co.insinyur.irreseller.utils.afterTextChanged
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel
import kotlinx.android.synthetic.main.fragment_login.loading as pbLoading
import kotlinx.android.synthetic.main.fragment_login.login as btnLogin
import kotlinx.android.synthetic.main.fragment_login.password as txtPassword
import kotlinx.android.synthetic.main.fragment_login.setting as btnSetting
import kotlinx.android.synthetic.main.fragment_login.username as txtUsername


class LoginFragment : BaseFragment() {

    private val viewModel: LoginViewModel by viewModel()
    private var job: Job? = null

    override fun onDestroy() {
        job?.cancel()
        super.onDestroy()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loginFormState.observe(this, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            btnLogin.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                txtUsername.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                txtPassword.error = getString(loginState.passwordError)
            }
        })

        viewModel.message.observe(this, { result ->
            if (result != null) {
                showMessage(result)
            }
        })

        viewModel.loading.observe(this, { result ->
            if (result != null) {
                pbLoading.visibility = result
            }
        })

        txtUsername.afterTextChanged {
            viewModel.loginDataChanged(
                txtUsername.text.toString(),
                txtPassword.text.toString()
            )
        }

        txtPassword.apply {
            afterTextChanged {
                viewModel.loginDataChanged(
                    txtUsername.text.toString(),
                    txtPassword.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        job = login(txtUsername.text.toString(), txtPassword.text.toString())
                }
                false
            }

            btnLogin.setOnClickListener {
                job = login(txtUsername.text.toString(), txtPassword.text.toString())
            }
        }

        btnSetting.setOnClickListener {
            val bottomSheetFragment = SettingFragment()
            bottomSheetFragment.show(
                requireActivity().supportFragmentManager, bottomSheetFragment.tag
            )
        }
    }

    private fun showMessage(result: String) {
        Toast.makeText(requireContext(), result, Toast.LENGTH_SHORT).show()
    }


    private fun login(username: String, password: String) = CoroutineScope(Dispatchers.Main).launch {
        val request = LoginRequest(username, password)
        viewModel.login(resources.getStringArray(R.array.urls)[0], request)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_login
    }
}