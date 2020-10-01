databaseChangeLog = {
	include file: 'core/2016-01-12-initial-db-state.groovy'
	include file: 'core/2016-01-28-streamr-map-module.groovy'
	include file: 'core/2016-02-05-color-modules.groovy'
	include file: 'core/2016-02-04-rate-limit-module.groovy'
	include file: 'core/2016-02-04-stream-module-js-module-change.groovy'
	include file: 'core/2016-01-13-input-modules-added.groovy'
	include file: 'core/2016-01-13-api-feature.groovy'
	include file: 'core/2016-01-21-replace-running-and-saved-signal-paths-with-canvas.groovy'
	include file: 'core/2016-02-02-mongodb-feed.groovy'
	include file: 'core/2016-02-25-feed-data-range-provider.groovy'
	include file: 'core/2016-01-13-permission-feature.groovy'
	include file: 'core/2016-02-09-permissions-for-signupinvites.groovy'
	include file: 'core/2016-02-10-remove-feeduser-modulepackageuser.groovy'
	include file: 'core/2016-02-19-add-sharespec-test-data.groovy'
	include file: 'core/2016-03-03-add-anonymous-access.groovy'
	include file: 'core/2016-03-07-replace-default-feed-mpkg-with-anonymous-permissions.groovy'
	include file: 'core/2016-03-09-eliminate-canvas-shared.groovy'
	include file: 'core/2016-03-02-map-modules.groovy'
	include file: 'core/2016-03-22-fix-input-modules-json-help.groovy'
	include file: 'core/2016-03-07-serialized-field-to-blob.groovy'
	include file: 'core/2016-03-08-map-modules-2.groovy'
	include file: 'core/2016-03-24-map-modules-3.groovy'
	include file: 'core/2016-03-28-constant-map.groovy'
	include file: 'core/2016-03-29-change-canvas-stream-modules-id.groovy'
	include file: 'core/2016-03-17-drop-ui-channel.groovy'
	include file: 'core/2016-02-04-scheduler-module-added.groovy'
	include file: 'core/2016-03-18-add-http-module.groovy'
	include file: 'core/2016-05-09-boolean-modules.groovy'
	include file: 'core/2016-05-09-stream-date-created-and-last-updated.groovy'
	include file: 'core/2016-05-20-add-sql-module.groovy'
	include file: 'core/2016-05-23-add-list-table-module.groovy'
	include file: 'core/2016-06-07-test-dashboard.groovy'
	include file: 'core/2016-06-08-new-variadic-modules.groovy'
	include file: 'core/2016-06-10-constant-list.groovy'
	include file: 'core/2016-06-10-new-moving-average-module.groovy'
	include file: 'core/2016-06-13-update-test-resources.groovy'
	include file: 'core/2016-06-13-filter-map-module.groovy'
	include file: 'core/2016-06-14-collect-from-maps-module.groovy'
	include file: 'core/2016-04-06-tours.groovy'
	include file: 'core/2016-06-15-new-event-table-module.groovy'
	include file: 'core/2016-07-06-twitter-feed.groovy'
	include file: 'core/2016-08-08-drop-unique-constraint-on-signupinvite.groovy'
	include file: 'core/2016-08-03-add-stream-modules.groovy'
	include file: 'core/2016-08-22-list-modules.groovy'
	include file: 'core/2016-08-22-get-from-list-module.groovy'
	include file: 'core/2016-08-22-each-with-index-module.groovy'
	include file: 'core/2016-05-24-add-string-template-module.groovy'
	include file: 'core/2016-09-07-time-of-event-module.groovy'
	include file: 'core/2016-03-10-expression-module.groovy'
	include file: 'core/2016-08-19-foreach-module-js.groovy'
	include file: 'core/2016-08-29-useful-list-modules.groovy'
	include file: 'core/2016-08-30-test-fixtures-foreach-subcanvas.groovy'
	include file: 'core/2016-09-12-format-number-module.groovy'
	include file: 'core/2016-03-21-random-modules.groovy'
	include file: 'core/2016-12-08-moving-window-module.groovy'
	include file: 'core/2016-12-10-export-csv-module.groovy'
	include file: 'core/2016-12-15-clock-module-update.groovy'
	include file: 'core/2017-01-17-xor-module.groovy'
	include file: 'core/2016-09-29-new-data-pipeline.groovy'
	include file: 'core/2017-03-12-ui-channel-streams.groovy'
	include file: 'core/2017-01-20-separate-serialization-domain-class.groovy'
	include file: 'core/2017-03-13-add-key-domain-object.groovy'
	include file: 'core/2017-03-22-migrate-stream-api-keys-to-key-domain.groovy'
	include file: 'core/2017-03-23-migrate-user-api-keys-to-key-domain.groovy'
	include file: 'core/2017-03-08-list-to-events-module.groovy'
	include file: 'core/2017-02-15-update-map-module.groovy'
	include file: 'core/2017-02-15-add-image-map-module.groovy'
	include file: 'core/2017-05-15-getorcreatestream-module.groovy'
	include file: 'core/2017-03-10-string-to-number-module.groovy'
	include file: 'core/2017-05-30-mqtt-module.groovy'
	include file: 'core/2017-01-20-ethereum-call-module.groovy'
	include file: 'core/2017-02-08-ethereum-contract-template-paybyuse.groovy'
	include file: 'core/2017-02-16-ethereum-contract-constant-module.groovy'
	include file: 'core/2017-03-16-ethereum-contract-template-binarybetting.groovy'
	include file: 'core/2017-03-28-ethereum-get-events-module.groovy'
	include file: 'core/2017-04-20-verify-signature-module.groovy'
	include file: 'core/2017-05-11-create-table-integration-key.groovy'
	include file: 'core/2017-05-30-migrate-eth-accounts-as-integration-keys.groovy'
	include file: 'core/2017-11-10-fix-run-canvas-spec.groovy'
	include file: 'core/2017-11-23-encrypt-private-keys.groovy'
	include file: 'core/2018-01-11-require-all-module.groovy'
	include file: 'core/2018-01-24-login-challenge.groovy'
	include file: 'core/2017-05-31-dashboard-layout-field.groovy'
	include file: 'core/2017-06-01-dashboard-id-to-string.groovy'
	include file: 'core/2017-06-16-dashboard-item-field-update.groovy'
	include file: 'core/2018-01-24-set-webcomponent-of-existing-canvas-modules.groovy'
	include file: 'core/2018-01-24-set-webcomponent-of-imagemap-module.groovy'
	include file: 'core/2017-12-21-domain-fields-for-permissions.groovy'
	include file: 'core/2018-01-16-remove-user-from-domain-objects.groovy'
	include file: 'core/2018-01-29-remove-mongodb-and-twitter-feeds.groovy'
	include file: 'core/2018-01-31-mqtt-help-text-update.groovy'
	include file: 'core/2018-02-12-add-anonymous-index-to-permission.groovy'
	include file: 'core/2018-02-28-bytearray-decoding-modules.groovy'
	include file: 'core/2018-02-22-drop-class-column-from-stream.groovy'
	include file: 'core/2018-02-27-add-id-in-service-to-integration-key.groovy'
	include file: 'core/2018-03-05-fix-canvas-spec.groovy'
	include file: 'core/2018-03-07-remove-template-field-of-feed.groovy'
	include file: 'core/2018-02-11-marketplace-domain-objects.groovy'
	include file: 'core/2018-02-18-add-devops-role.groovy'
	include file: 'core/2018-02-20-test-data-for-product-api.groovy'
	include file: 'core/2018-02-27-add-subscription.groovy'
	include file: 'core/2018-03-27-add-field-score-to-product.groovy'
	include file: 'core/2018-04-04-add-product-thumbnailurl.groovy'
	include file: 'core/2018-04-06-add-altname-to-expression-module.groovy'
	include file: 'core/2018-04-09-add-owner-to-product.groovy'
	include file: 'core/2018-04-09-allow-nulls-in-certain-product-fields.groovy'
	include file: 'core/2018-04-12-free-and-paid-subscriptions.groovy'
	include file: 'core/2018-04-16-add-ends-at-field-to-permission.groovy'
	include file: 'core/2018-04-25-product-owner-to-user.groovy'
	include file: 'core/2018-04-27-insert-categories.groovy'
	include file: 'core/2018-04-29-fix-webcomponent-deserialization-bug.groovy'
	include file: 'core/2018-05-02-test-data-products-subscriptions.groovy'
	include file: 'core/2018-05-23-fix-product-images.groovy'
	include file: 'core/2018-07-02-rm-feedfile-table.groovy'
	include file: 'core/2018-07-02-rm-feed-bundled-feed-files.groovy'
	include file: 'core/2018-07-02-remove-feed-discoveryutilclass.groovy'
	include file: 'core/2018-07-03-rm-feed-start-on-demand.groovy'
	include file: 'core/2018-07-12-canvas-started-by.groovy'
	include file: 'core/2018-08-30-test-data-for-stream-api-tests.groovy'
	include file: 'core/2018-09-26-unify-domain.groovy'
	include file: 'core/2018-09-20-rm-user-timezone.groovy'
	include file: 'core/2018-11-27-add-requireSignedData-field.groovy'
	include file: 'core/2018-12-18-change-default-value-require-signed-data.groovy'
	include file: 'core/2019-01-17-date-created-login.groovy'
	include file: 'core/2019-01-30-rename-key-provider.groovy'
	include file: 'core/2019-01-31-new-stream-fields.groovy'
	include file: 'core/2019-02-15-user-avatar.groovy'
	include file: 'core/2019-03-19-stream-canvas-example.groovy'
	include file: 'core/2019-03-25-rm-canvas-example.groovy'
	include file: 'core/2019-04-01-add-inbox-streams.groovy'
	include file: 'core/2019-04-29-add-Permission-parent-ref.groovy'
	include file: 'core/2019-03-26-new-ethereum-call-module.groovy'
	include file: 'core/2019-04-08-ethereumj-compilation-web3j-deploy.groovy'
	include file: 'core/2019-04-12-cp-domain.groovy'
	include file: 'core/2019-06-03-unique-ethereum-addresses.groovy'
	include file: 'core/2019-04-26-drop-feed.groovy'
	include file: 'core/2019-06-12-stream-inactivity.groovy'
	include file: 'core/2019-07-29-enable-stream-module.groovy'
	include file: 'core/2019-08-27-rm-modwt.groovy'
	include file: 'core/2019-09-13-product-pendingchanges.groovy'
	include file: 'core/2019-09-11-getethbalance-module.groovy'
	include file: 'core/2019-09-27-stream-require-encrypted-data.groovy'
	include file: 'core/2019-09-27-alter-pending-changes.groovy'
	include file: 'core/2019-11-02-rm-old-ethereum-modules.groovy'
	include file: 'core/2020-01-24-rename-community-to-data-union.groovy'
	include file: 'core/2020-01-24-new-permissions.groovy'
	include file: 'core/2020-02-11-rm-module-package-domain-class.groovy'
	include file: 'core/2020-02-25-test-data-products-permissions.groovy'
	include file: 'core/2020-03-05-new-product-fields-contact-terms-of-use.groovy'
	include file: 'core/2020-04-01-rm-tour-user.groovy'
	include file: 'core/2020-04-15-user-refactor-username-add-email.groovy'
	include file: 'core/2020-08-07-mv-secuser-user.groovy'
	include file: 'core/2020-08-24-add-user-signup-method.groovy'
	include file: 'core/2020-09-11-add-stream-storage-node.groovy'
	include file: 'core/2020-09-15-add-DU-version-field-to-product.groovy'
	include file: 'core/2020-10-01-remove-anonymous-keys-delete-and-edit-permissions.groovy'
}
