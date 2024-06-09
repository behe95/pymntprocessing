import { useEffect } from "react";

export default function Content({setData}) {


    useEffect(() => {
        console.log("ON mount");
        console.log(setData)

        return () => {
            console.log("Unmount");            
            setData([]);
        };
    }, [])

    return (
        
    <div id="content">
        <p id="remove">Hello</p>

    </div>
    );
}