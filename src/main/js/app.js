import {UploadForm} from "./uploadForm";
import {FileWithLinksList, PdfRender} from "./fileWithLinksList";
import axios from 'axios';
import process from "webpack-cli/bin/.eslintrc";

const React = require('react');
const ReactDOM = require('react-dom');


class App extends React.Component {

    constructor(props) {
        super(props);
        this.state = {fileWithLinks: []};
    }

    componentDidMount() {
        console.log("component mounted");
        const url = 'https://localstack-e61o.onrender.com/links';
        const config = {};
        axios.get(url, config).then((response) => {
            console.log(response.data);
            console.log("fileWithLinks", response.data.fileWithLinks)
            this.setState({fileWithLinks: response.data.fileWithLinks});
        });
    }

    render() {
        return (
            <div>
                <UploadForm/>
                <hr/>
                <FileWithLinksList
                    fileWithLinks={this.state.fileWithLinks}
                />
                <hr/>
                <PdfRender/>
            </div>
        )
    }
}


ReactDOM.render(
    <App/>
    ,
    document.getElementById('react')
)

// class UploadForm extends React.Component
// {
//     constructor(props) {
//         super(props);
//         this.state = {value: ''};
//
//         this.handleChange = this.handleChange.bind(this);
//         // this.handleSubmit = this.handleSubmit.bind(this);
//     }
//
//     handleChange(event) {
//         const fileElement = event.target;
//         const ONE_MEGABYTE = 1024;
//         // check file selected
//         if (fileElement.files.length > 0) {
//             const fileSize = fileElement.files.item(0).size;
//             const sizeInMb = Math.round((fileSize / ONE_MEGABYTE));
//             console.log(sizeInMb)
//             // The size of the file should be <= 1MB
//             if (sizeInMb < ONE_MEGABYTE) {
//                 if (document.getElementById('upload-button').disabled === true) {
//                     document.getElementById('upload-button').disabled = false;
//                 }
//             } else {
//                 document.getElementById('upload-button').disabled = true;
//                 alert("File too Big, please select a file less than 1MB");
//             }
//         }
//         this.setState({value: event.target.value});
//     }
//
//     // handleSubmit(event) {
//     //     alert('A name was submitted: ' + this.state.value);
//     //     event.preventDefault();
//     // }
//
//     render() {
//         return (
//             <form method="POST" encType="multipart/form-data" action="/upload">
//                 <table>
//                     <tr>
//                         <td>
//                             {/*<label>*/}
//                             Upload File:
//                         </td>
//                         <td>
//                             <input id="file" type="file" name="file" accept=".pdf" value={this.state.value}
//                                    onChange={this.handleChange}/>
//                         </td>
//                         {/*</label>*/}
//                     </tr>
//                     <tr>
//                         <td></td>
//                         <td>
//                             <input type="submit" value="Upload" id="upload-button"/>
//                         </td>
//                     </tr>
//                 </table>
//             </form>
//         );
//     }
// }


// class Form extends React.Component {
//     render() {
//         return (<div>
//             <form method="POST" encType="multipart/form-data" action="/upload">
//                 <table>
//                     <tr>
//                         <td>File to upload:</td>
//                         {/*<td><input id="file" type="file" name="file" onChange=validateFileSizeAndEnableUpload*/}
//                         {/*           accept=".pdf"/></td>*/}
//                         {/* <td><input id="file" type="file" name="file" onchange="validateFileSizeAndEnableUpload()" accept=".pdf" /></td> */}
//                     </tr>
//                     <tr>
//                         <td></td>
//                         <td><input type="submit" value="Upload" id="upload-button"/></td>
//                     </tr>
//                 </table>
//             </form>
//         </div>)
//     }
// }