import React, {useState} from "react";
import {useAuth} from "../contexts/AuthContext";
import {signIn} from "../api/Api";

function SignIn() {
    const {googleLogin} = useAuth();
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')

    const handleEmailChange = event => {
        setEmail(event.target.value)
    }

    const handlePasswordChange = event => {
        setPassword(event.target.value)
    }

    const handleSubmit = async event => {
        event.preventDefault()
        if (email === '' || password === '') {
            alert("Email and password must not be empty")
            return;
        }
        await signIn(email, password)
            .then(_ => {
                window.location.href = "/";
            })
            .catch(err => {
                if (err.response.status === 403) {
                    alert("Invalid credentials")
                } else {
                    alert(err)
                }
            })
    }
    return (
        <center>
            <form onSubmit={handleSubmit}>
                <label>
                    Email:
                    <input type="email" onChange={handleEmailChange} value={email}/>
                </label><br/>
                <label>
                    Password:
                    <input type="password" onChange={handlePasswordChange} value={password}/>
                </label><br/>
                <input type="submit" value="Sign in"/>
            </form>
            <button onClick={googleLogin}>Sign in with Google</button>
        </center>
    )
}

export default SignIn;