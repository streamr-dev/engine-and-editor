package core
databaseChangeLog = {

	changeSet(author: "eric", id: "migrate-user-api-keys-to-key-domain-1") {
		grailsChange {
			change {
				sql.eachRow("SELECT id, username, api_key FROM sec_user") { row ->
					def userId = row['id']
					def keyId = row['api_key']
					def username = row['username']
					def name = "Key for " + username
					sql.execute('INSERT INTO `key` (id, version, name, user_id) VALUES (?, 0, ?, ?)', keyId, name, userId)
				}
			}
		}
	}

	changeSet(author: "eric", id: "migrate-user-api-keys-to-key-domain-2") {
		dropIndex(indexName: "apiKey_index", tableName: "sec_user")
	}

	changeSet(author: "eric", id: "migrate-user-api-keys-to-key-domain-3") {
		dropIndex(indexName: "api_key_uniq_1452618583160", tableName: "sec_user")
	}

	changeSet(author: "eric", id: "migrate-user-api-keys-to-key-domain-4") {
		dropColumn(columnName: "api_key", tableName: "sec_user")
	}
}
