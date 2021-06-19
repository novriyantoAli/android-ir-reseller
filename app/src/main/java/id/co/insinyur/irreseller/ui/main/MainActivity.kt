package id.co.insinyur.irreseller.ui.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import com.afollestad.materialdialogs.MaterialDialog
import id.co.insinyur.irreseller.R
import id.co.insinyur.irreseller.ui.login.LoginFragment
import id.co.insinyur.irreseller.ui.packages.PackageFragment
import id.co.insinyur.irreseller.ui.transaction.TransactionFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModel()

    private lateinit var loginFragment: LoginFragment
    private lateinit var transactionFragment: TransactionFragment
    private lateinit var packageFragment: PackageFragment

    private var job: Job? = null

    override fun onDestroy() {
        job?.cancel()
        super.onDestroy()
    }

    // OVERRIDE ---
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loginFragment = LoginFragment()
        transactionFragment = TransactionFragment()
        packageFragment = PackageFragment()

        viewModel.stateLogin.observe(this, Observer {
            if (it != null){
                displayTransactionFragment()
                return@Observer
            }
            // show login fragment
            displayLoginFragment()
        })

        viewModel.balance.observe(this, {
            if (it != null) {
                val message = "Your Balance is: ${it.balance}"
                showForm(R.string.recent_balance, message)
            }
        })

    }

    private fun showForm(title: Int, message: String) {
        MaterialDialog(this).show {
            title(title)
            message(null, message)
            cancelable(false)
            positiveButton(R.string.ok){ return@positiveButton }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        // Inflate the menu; this adds items to the action bar if it is present.
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_search, menu)
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true
    }

    private fun displayLoginFragment() {
        this.supportActionBar?.hide()


        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        if (loginFragment.isAdded) { // if the fragment is already in container
            ft.show(loginFragment)
        } else { // fragment needs to be added to frame container
            ft.add(R.id.activity_main_container, loginFragment, "loginFragment")
        }
        // Hide fragment B
        if (transactionFragment.isAdded) {
            ft.hide(transactionFragment)
        }
        if (packageFragment.isAdded) {
            ft.hide(packageFragment)
        }
        // Commit changes
        ft.commit()
    }

    private fun displayTransactionFragment() {
        this.supportActionBar?.show()

        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        if (transactionFragment.isAdded) { // if the fragment is already in container
            ft.show(transactionFragment)
        } else { // fragment needs to be added to frame container
            ft.add(R.id.activity_main_container, transactionFragment, "transactionFragment")
        }
        // Hide fragment B
        if (loginFragment.isAdded) {
            ft.hide(loginFragment)
        }
        if (packageFragment.isAdded) {
            ft.hide(packageFragment)
        }
        // Commit changes
        ft.commit()
    }

    private fun displayPackageFragment() {
        this.supportActionBar?.show()

        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        if (packageFragment.isAdded) { // if the fragment is already in container
            ft.show(packageFragment)
        } else { // fragment needs to be added to frame container
            ft.add(R.id.activity_main_container, packageFragment, "packageFragment")
        }
        // Hide fragment B
        if (loginFragment.isAdded) {
            ft.hide(loginFragment)
        }
        if (transactionFragment.isAdded) {
            ft.hide(transactionFragment)
        }
        // Commit changes
        ft.commit()
    }

    private fun checkBalance() = CoroutineScope(Dispatchers.Main).launch {
        viewModel.getBalance(resources.getStringArray(R.array.urls)[1])
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> {
                val searchView = item.actionView as? SearchView
                searchView?.queryHint = getString(R.string.search)
            }
            R.id.menu_balance -> {
                job = checkBalance()
            }
            R.id.menu_transaction -> {
                displayTransactionFragment()
            }
            R.id.menu_package -> {
                displayPackageFragment()
            }
            R.id.menu_logout -> {
                viewModel.logout()
            }
        }
        return true
    }

//    private fun configureToolbar(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.menu_search, menu)
//
//        val searchItem = menu?.findItem(R.id.action_search)
//        val searchView = searchItem?.actionView as? SearchView
//        searchView?.queryHint = getString(R.string.search)
//        return true
//    }
}
