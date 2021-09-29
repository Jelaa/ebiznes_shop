import axios from "axios";
import {BACKEND_BASE_URL} from "../const/Const";
import Cookies from 'js-cookie';

const CSRF_TOKEN_COOKIE_NAME = "csrfToken"
const JSON_HEADERS = {
    "Content-Type": "application/json; charset=utf-8"
}

axios.defaults.withCredentials = true

export const signOut = () => {
    return postCall("/signOut")
}

export const signIn = (email, password) => {
    const body = {
        email : email,
        password : password
    }
    return postCall("/signIn", body, JSON_HEADERS)
}

export const signUp = (email, password) => {
    const body = {
        email : email,
        password : password
    }
    return postCall("/signUp", body, JSON_HEADERS)
}

function postCall(url, data = {}, headers = {}) {
    const fullUrl = buildUrl(url)
    const allHeaders = buildHeadersWithCsrfToken(headers)
    const config = buildConfigWithHeaders(allHeaders)
    return axios.post(fullUrl, data, config)
}

function buildUrl(url) {
    return BACKEND_BASE_URL + url
}

function buildHeadersWithCsrfToken(headers) {
    return Object.assign({}, headers, {
        "Csrf-Token": getCsrfToken()
    })
}

function getCsrfToken() {
    return Cookies.get(CSRF_TOKEN_COOKIE_NAME)
}

function buildConfigWithHeaders(headers) {
    return {
        headers: headers
    }
}