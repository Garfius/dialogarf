package io.github.zohrevand.dialogue.core.xmpp

import android.app.Service
import android.content.Intent
import android.os.IBinder
import dagger.hilt.android.AndroidEntryPoint
import io.github.zohrevand.dialogue.core.xmpp.collector.AccountsCollector
import io.github.zohrevand.dialogue.core.xmpp.notification.NotificationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class XmppService : Service() {

    private val scope = CoroutineScope(SupervisorJob())

    @Inject
    lateinit var xmppManager: XmppManager

    @Inject
    lateinit var accountsCollector: AccountsCollector

    @Inject
    lateinit var notificationManager: NotificationManager

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        scope.launch {
            xmppManager.getAuthenticatedStream().collectLatest { isAuthenticated ->

            }
        }

        observeAccountsStream()

        return super.onStartCommand(intent, flags, startId)
    }

    private fun observeAccountsStream() {
        scope.launch {
            accountsCollector.collectAccounts(
                onNewLogin = { scope.launch { xmppManager.login(it) } },
                onNewRegister = { scope.launch { xmppManager.register(it) } },
            )
        }
    }

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}