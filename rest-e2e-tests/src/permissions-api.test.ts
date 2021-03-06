import {assert} from 'chai'
import Streamr from './streamr-api-clients'
import {Stream, StreamPermision, StreamrClient} from 'streamr-client'
import {getSessionToken, getStreamrClient} from './test-utilities'

describe('Permissions API', () => {
    const me = StreamrClient.generateEthereumAccount()
    const existingUser = StreamrClient.generateEthereumAccount()

    before(async () => {
        // Make sure the "existingUser" exists by logging them in
        await getSessionToken(existingUser)
    })

    describe('POST /api/v1/streams/{id}/permissions', () => {
        let stream: Stream

        before(async () => {
            stream = await getStreamrClient(me).createStream({
                id: `/test-stream/${Date.now()}`,
                name: `permissions-api.test.js-${Date.now()}`
            })
        })

        it('can grant a permission to an existing user using email address', async () => {
            const response = await Streamr.api.v1.streams
                .grant(stream.id, 'tester1@streamr.com', 'stream_get')
                .withAuthenticatedUser(me)
                .call()
            assert.equal(response.status, 200)
        })

        it('can grant a permission to a non-existing user using email address', async () => {
            const response = await Streamr.api.v1.streams
                .grant(stream.id, `${Date.now()}@foobar.invalid`, 'stream_get')
                .withAuthenticatedUser(me)
                .call()
            assert.equal(response.status, 200)
        })

        it('can grant a permission to an existing user using Ethereum address', async () => {
            const response = await Streamr.api.v1.streams
                .grant(stream.id, existingUser.address, 'stream_get')
                .withAuthenticatedUser(me)
                .call()
            assert.equal(response.status, 200)
        })

        it('can grant a permission to a non-existing user using Ethereum address', async () => {
            const response = await Streamr.api.v1.streams
                .grant(stream.id, StreamrClient.generateEthereumAccount().address, 'stream_get')
                .withAuthenticatedUser(me)
                .call()
            assert.equal(response.status, 200)
        })
    })

    describe('DELETE /api/v1/streams/{streamId}/permissions/{permissionId}', function () {
        let stream: Stream

        before(async function () {
            stream = await getStreamrClient(me).createStream({
                id: `/test-stream/${Date.now()}`,
                name: `permissions-api.test.js-delete-${Date.now()}`
            })
        })

        it('delete a permission', async function () {
            const permissionResponse = await Streamr.api.v1.streams
                .grant(stream.id, StreamrClient.generateEthereumAccount().address, 'stream_get')
                .withAuthenticatedUser(me)
                .call()
            const permissionGet = await permissionResponse.json()
            assert.equal(permissionResponse.status, 200)

            const response = await Streamr.api.v1.streams
                .deletePermission(stream.id, permissionGet.id)
                .withAuthenticatedUser(me)
                .call()
            assert.equal(response.status, 204)
        })

        it('deleting last share permission is not allowed', async function () {
            const ownPermissionsResponse = await Streamr.api.v1.streams
                .getOwnPermissions(stream.id)
                .withAuthenticatedUser(me)
                .call()
            const ownPermissions = await ownPermissionsResponse.json()
            assert.equal(ownPermissionsResponse.status, 200)
            const sharePermission = ownPermissions.filter((permission: StreamPermision) => permission.operation === 'stream_share')[0]

            const response = await Streamr.api.v1.streams
                .deletePermission(stream.id, sharePermission.id)
                .withAuthenticatedUser(me)
                .call()
            assert.equal(response.status, 500)
        })

        it('deletes permissions', async function () {
            const ownPermissionsResponse = await Streamr.api.v1.streams
                .getOwnPermissions(stream.id)
                .withAuthenticatedUser(me)
                .call()
            const ownPermissions = await ownPermissionsResponse.json()
            assert.equal(ownPermissionsResponse.status, 200)
            const permissionsToDelete = ownPermissions.filter((permission: StreamPermision) => permission.operation !== 'stream_share')

            permissionsToDelete.forEach(async function (permission: StreamPermision) {
                const response = await Streamr.api.v1.streams
                    .deletePermission(stream.id, permission.id)
                    .withAuthenticatedUser(me)
                    .call()
                assert.equal(response.status, 204, `delete permission unexpected status ${response.status} for ${JSON.stringify(permission)}`)
            })
        })
    })
})
