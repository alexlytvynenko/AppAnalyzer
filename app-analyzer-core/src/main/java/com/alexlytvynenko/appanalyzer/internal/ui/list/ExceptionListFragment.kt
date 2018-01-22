package com.alexlytvynenko.appanalyzer.internal.ui.list

import android.os.Bundle
import android.view.View
import com.alexlytvynenko.appanalyzer.internal.NetworkAnalyzerInternal
import com.alexlytvynenko.appanalyzer.internal.entity.LogEntity
import com.alexlytvynenko.appanalyzer.internal.ui.list.base.BaseListAdapter
import com.alexlytvynenko.appanalyzer.internal.ui.list.base.BaseListFragment
import com.alexlytvynenko.appanalyzer.internal.ui.list.viewHolder.ItemViewHolder
import kotlinx.android.synthetic.main.fragment_list.*

/**
 * Created by alex_litvinenko on 24.10.17.
 */
internal class ExceptionListFragment : BaseListFragment<LogEntity>() {

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity.title = "Exceptions in ${activity.packageName}"
    }

    override fun onItemClicked(entity: ItemViewHolder) {}

    override fun isFeatureEnabled() = !NetworkAnalyzerInternal.disabledExceptions

    override fun getErrorText() = "Exceptions are disabled. To be able to catch exceptions go to the <font color='#A9B7C6'>NetworkAnalyzer</font> installation and call\n<font color='#A9B7C6'>NetworkAnalyzer.disabledExceptions(</font><font color='#CC7832'>false</font><font color='#A9B7C6'>)</font>"

    override fun loadItems(): List<LogEntity> {
        var exceptions = NetworkAnalyzerInternal.loadLogsFromDatabase(true)
        if (exceptions == null) {
            exceptions = arrayListOf()
        }
        return exceptions.sortedWith(LogEntity.dateComparator)
    }

    override fun filterItems(query: String): List<LogEntity> =
            items.filter { it.tag.contains(query, true) || it.text.contains(query, true) }

    override fun removeItem(entity: LogEntity) {
        NetworkAnalyzerInternal.deleteLogFromDatabase(entity, true)
    }

    override fun removeAll() {
        NetworkAnalyzerInternal.deleteLogsFromDatabase(true)
    }

    override fun share() {
        val text = StringBuilder("")
        (list.adapter as BaseListAdapter<*>).entities.forEach {
            it as LogEntity
            text.append(it.toShareData())
        }
        val file = NetworkAnalyzerInternal.saveToFile(activity, text.toString())
        NetworkAnalyzerInternal.shareFile(activity, file, "Exceptions")
    }
}