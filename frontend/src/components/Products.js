import React, {useEffect, useState} from "react";
import {getProducts} from "../api/Api";
import Product from "./Product";

function Products() {
    const [products, setProduct] = useState();
    useEffect(() => {
        getProducts()
            .then(response => {
                if (response.status === 200) {
                    let parsedProducts = response.data.map(product => <Product key={product.id} product={product}/>)
                    setProduct(parsedProducts)
                } else {
                    alert(response)
                }
            })
            .catch(err => alert(err))
    }, [])
    return (
        products === undefined ? <center>loading</center> :
            <center>
                <table>
                    <thead>
                    <tr>
                        <th>Product</th>
                        <th>Description</th>
                        <th>Category</th>
                    </tr>
                    </thead>
                    <tbody>
                    {products}
                    </tbody>
                </table>
            </center>
    )
}

export default Products;