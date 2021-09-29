import React, {useState} from "react";
import {signUp} from "../api/Api";

function SignUp() {
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
        await signUp(email, password)
            .then(_ => {
                alert("User created")
                window.location.href = "/";
            })
            .catch(err => {
                if (err.response.status === 403) {
                    alert("User already exists")
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
                <input type="submit" value="Sign up"/>
            </form>
        </center>
    )
}

export default SignUp;