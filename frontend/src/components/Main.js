import React from "react";
import {useAuth} from "../contexts/AuthContext";
import Products from "./Products";

function Main() {
    const {checkIfLogged} = useAuth()

    return (
        checkIfLogged() ? <Products /> : null
    )
}

export default Main;