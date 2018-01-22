package com.alexlytvynenko.appanalyzer.internal.ui.list

import android.os.Bundle
import android.view.View
import com.alexlytvynenko.appanalyzer.internal.NetworkAnalyzerInternal
import com.alexlytvynenko.appanalyzer.internal.entity.RequestEntity
import com.alexlytvynenko.appanalyzer.internal.ui.DisplayNetworkActivity
import com.alexlytvynenko.appanalyzer.internal.ui.list.base.BaseListAdapter
import com.alexlytvynenko.appanalyzer.internal.ui.list.base.BaseListFragment
import com.alexlytvynenko.appanalyzer.internal.ui.list.viewHolder.ItemViewHolder
import com.alexlytvynenko.appanalyzer.internal.ui.list.viewHolder.getData
import kotlinx.android.synthetic.main.fragment_list.*

/**
 * Created by alex_litvinenko on 12.10.17.
 */
internal class RequestListFragment : BaseListFragment<RequestEntity>() {

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity.title = "Requests in ${activity.packageName}"
    }

    override fun onItemClicked(entity: ItemViewHolder) {
        (activity as DisplayNetworkActivity).openDetailsFragment(entity.getData())
    }

    override fun isFeatureEnabled() = NetworkAnalyzerInternal.isNetworkInterceptorInited
            && !NetworkAnalyzerInternal.disabledRequests

    override fun getErrorText() = when {
        !NetworkAnalyzerInternal.isNetworkInterceptorInited -> "<font color='#A9B7C6'>HttpNetworkAnalyzerInterceptor</font> isn\'t set. To be able to listen requests go to <font color='#A9B7C6'>OkHttpClient</font> initialization and add <font color='#A9B7C6'>HttpNetworkAnalyzerInterceptor</font> to interceptors."
        else -> "Requests are disabled. To be able to listen requests go to the <font color='#A9B7C6'>NetworkAnalyzer</font> installation and call\n<font color='#A9B7C6'>NetworkAnalyzer.disabledRequests(</font><font color='#CC7832'>false</font><font color='#A9B7C6'>)</font>"
    }

    override fun loadItems(): List<RequestEntity> {
        var requests = NetworkAnalyzerInternal.loadRequestsFromDatabase()
        if (requests == null) {
            requests = arrayListOf()
        }
        return requests.sortedWith(RequestEntity.dateComparator)
    }

    override fun filterItems(query: String): List<RequestEntity> = items

    override fun removeItem(entity: RequestEntity) {
        NetworkAnalyzerInternal.deleteRequestFromDatabase(entity)
    }

    override fun removeAll() {
        NetworkAnalyzerInternal.deleteRequestsFromDatabase()
    }

    override fun share() {
        val text = StringBuilder("")
        (list.adapter as BaseListAdapter<*>).entities.forEach {
            it as RequestEntity
            text.append(it.toShareData())
        }
        val file = NetworkAnalyzerInternal.saveToFile(activity, text.toString())
        NetworkAnalyzerInternal.shareFile(activity, file, "Requests")
    }
}