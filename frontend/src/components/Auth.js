import React from 'react';
import {useAuth} from "../contexts/AuthContext";
import {Link} from "react-router-dom";

function Auth() {
    const {checkIfLogged, LogOut} = useAuth()
    return (
        checkIfLogged() ?
            <div>
                <center>You are already signed in.<button onClick={LogOut}>Sign out</button></center>
            </div> :
            <div>
                <center>You are not signed in. <Link to="/signIn">Sign in</Link> <Link to="/signUp">Sign up</Link></center>
            </div>
    );
}

export default Auth;