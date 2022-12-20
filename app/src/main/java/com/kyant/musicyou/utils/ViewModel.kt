package com.kyant.musicyou.utils

import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelProvider
import com.kyant.musicyou.App

@MainThread
inline fun <reified VM : ViewModel> ComponentActivity.appViewModels(
    noinline factoryProducer: (() -> ViewModelProvider.Factory)? = null
) = ViewModelLazy(
    viewModelClass = VM::class,
    storeProducer = { (applicationContext as App).viewModelStore },
    factoryProducer = factoryProducer ?: { defaultViewModelProviderFactory },
    extrasProducer = { defaultViewModelCreationExtras }
)
