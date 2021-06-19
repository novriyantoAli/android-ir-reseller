package id.co.insinyur.irreseller.di

import id.co.insinyur.irreseller.ui.login.LoginViewModel
import id.co.insinyur.irreseller.ui.main.MainViewModel
import id.co.insinyur.irreseller.ui.packages.PackageViewModel
import id.co.insinyur.irreseller.ui.setting.SettingViewModel
import id.co.insinyur.irreseller.ui.transaction.TransactionViewModel
import id.co.insinyur.irreseller.ui.user.search.SearchUserViewModel
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val viewModelModule = module {
//    viewModel {
//        SearchUserViewModel(get(), get())
//    }
    viewModel {
        LoginViewModel(get())
    }
    viewModel {
        MainViewModel(get())
    }
    viewModel {
        TransactionViewModel(get(), get(), get())
    }
    viewModel { PackageViewModel(get(), get()) }
    viewModel { SettingViewModel(get(), get()) }
}
