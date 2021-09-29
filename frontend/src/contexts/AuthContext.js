import React, { useEffect, useState } from "react";
import Cookies from 'js-cookie';
import {signOut} from "../api/Api";
import {BACKEND_BASE_URL} from "../const/Const";

export const AuthContext = React.createContext();
export const useAuth = () => React.useContext(AuthContext);

export const AuthContextProvider = ({children}) => {
    const [token, setToken] = useState("")

    const LogOut = async () => {
        await signOut()
            .then(_ => window.location.href = "/")
            .catch(_ => window.location.href = "/")
    }

    const checkIfLogged = () => {
        return !!token;
    }

    const getToken = () => {
        return token
    }

    const googleLogin = () => {
        window.location.href = BACKEND_BASE_URL + "/authenticate/google";
    };

    function refreshLoggedInfo() {
        const tokenValue = Cookies.get("authenticator")
        setToken(tokenValue)
    }

    useEffect(() => {
        refreshLoggedInfo()
    })

    return (
        <AuthContext.Provider value={{
            checkIfLogged,
            getToken,
            googleLogin,
            LogOut
        }}>
            {children}
        </AuthContext.Provider>
    );
}