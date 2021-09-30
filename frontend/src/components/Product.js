import React from "react";

const Product = ({product}) => {
    return (
        <tr>
            <td>{product.name}</td>
            <td>{product.description}</td>
            <td>{product.category.name}</td>
        </tr>
    )
}

export default Product;