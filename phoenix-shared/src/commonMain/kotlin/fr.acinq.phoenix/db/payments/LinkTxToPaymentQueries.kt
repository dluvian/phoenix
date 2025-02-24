/*
 * Copyright 2023 ACINQ SAS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.acinq.phoenix.db.payments

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import fr.acinq.bitcoin.TxId
import fr.acinq.phoenix.data.WalletPaymentId
import fr.acinq.phoenix.db.PaymentsDatabase
import kotlinx.coroutines.flow.*

class LinkTxToPaymentQueries(val database: PaymentsDatabase) {
    private val linkTxQueries = database.linkTxToPaymentQueries

    fun listUnconfirmedTxs(): Flow<List<ByteArray>> {
        return linkTxQueries.listUnconfirmed().asFlow().mapToList()
    }

    fun listWalletPaymentIdsForTx(txId: TxId): List<WalletPaymentId> {
        return linkTxQueries.getPaymentIdForTx(tx_id = txId.value.toByteArray()).executeAsList()
            .mapNotNull { WalletPaymentId.create(it.type, it.id) }
    }

    fun linkTxToPayment(txId: TxId, walletPaymentId: WalletPaymentId) {
        linkTxQueries.linkTxToPayment(tx_id = txId.value.toByteArray(), type = walletPaymentId.dbType.value, id = walletPaymentId.dbId)
    }

    fun setConfirmed(txId: TxId, confirmedAt: Long) {
        linkTxQueries.setConfirmed(tx_id = txId.value.toByteArray(), confirmed_at = confirmedAt)
    }

    fun setLocked(txId: TxId, lockedAt: Long) {
        linkTxQueries.setLocked(tx_id = txId.value.toByteArray(), locked_at = lockedAt)
    }
}