package core

import com.unifina.domain.IntegrationKey
import com.unifina.utils.IdGenerator
import com.unifina.security.ApiKeyConverter
import com.unifina.service.EthereumIntegrationKeyService
import com.unifina.utils.AlphanumericStringGenerator
import grails.converters.JSON

def getAccountAddress(String apiKeyId) {
	String privateKey = ApiKeyConverter.createEthereumPrivateKey(apiKeyId)
	return EthereumIntegrationKeyService.getAddress(privateKey)
}

databaseChangeLog = {

	// For each API key that is attached to a user:
	// - create a new IntegrationKey for the user
	changeSet(author: "teogeb", id: "convert-API-keys-to-Ethereum-IDs-1") {
		grailsChange {
			change {
				sql.eachRow('SELECT id, name, user_id FROM `key` where user_id is not null') { row ->
					String apiKeyId = row['id']
					String apiKeyName = row['name']
					String userId = row['user_id']
					String accountAddress = getAccountAddress(apiKeyId)
					String integrationKeyName = 'Converted from API key: ' + apiKeyName
					Date now = new Date()
					sql.executeInsert("INSERT INTO integration_key (id, version, name, json, user_id, service, id_in_service, date_created, last_updated) VALUE (?, ?, ?, ?, ?, ?, ?, ?, ?)", [
						IdGenerator.get(),
						0,
						integrationKeyName,
						([ address: accountAddress ] as JSON).toString(),
						userId,
						IntegrationKey.Service.ETHEREUM_ID.name(),
						accountAddress,
						now,
						now
					])
				}
			}
		}
	}

	// For each anonymous API key:
	// - create a new Ethereum user
	// - migrate the key’s permissions to that user
	changeSet(author: "teogeb", id: "convert-API-keys-to-Ethereum-IDs-2") {
		grailsChange {
			change {
				sql.eachRow('SELECT id, name, user_id FROM `key` where user_id is null') { row ->
					String apiKeyId = row['id']
					String accountAddress = getAccountAddress(apiKeyId)
					def insertResult = sql.executeInsert("INSERT INTO user (version, account_expired, account_locked, enabled, name, password, password_expired, username, signup_method) VALUE (?, ?, ?, ?, ?, ?, ?, ?, ?)", [
							0,
							false,
							false,
							true,
							"Anonymous User",
							AlphanumericStringGenerator.getRandomAlphanumericString(32),
							false,
							accountAddress,
							'UNKNOWN'  // TODO: what value we should use?
					]);
					int userId = insertResult[0][0]
					sql.execute("UPDATE permission SET user_id = ?, key_id = null WHERE key_id = ?", [ userId, apiKeyId])
				}
			}
		}
	}

	changeSet(author: "teogeb", id: "convert-API-keys-to-Ethereum-IDs-3") {
		dropForeignKeyConstraint(baseTableName: "permission", constraintName: "FKE125C5CF8EE35041")
	}

	changeSet(author: "teogeb", id: "convert-API-keys-to-Ethereum-IDs-4") {
		dropColumn(tableName: "permission", columnName: "key_id")
	}

	changeSet(author: "teogeb", id: "convert-API-keys-to-Ethereum-IDs-5") {
		dropTable(tableName: "key")
	}
}
