// @flow

import axios from 'axios'
import qs from 'qs'
import type { FormFields } from './types'

export const onInputChange = (callback: (string, any) => void, inputName: ?string) => (e: SyntheticInputEvent<EventTarget>) => {
    const value = e.target.type === 'checkbox' ? e.target.checked : e.target.value
    callback(inputName || e.target.name, value)
}

export const post = (url: string, data: FormFields, successWithError: boolean, xhr: boolean): Promise<*> => new Promise((resolve, reject) => {
    axios
        .post(url, qs.stringify(data), {
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                ...(xhr ? {
                    'X-Requested-With': 'XMLHttpRequest',
                } : {}),
            },
        })
        .then((response) => {
            if (successWithError && response.error) {
                reject(new Error(response.error))
            } else {
                resolve()
            }
        }, ({ response: { data } }) => {
            reject(new Error(data.error || 'Something went wrong'))
        })
})