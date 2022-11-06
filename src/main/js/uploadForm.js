import React from "react";
import axios from "axios";
import process from "webpack-cli/bin/.eslintrc";

export class UploadForm extends React.Component {
    constructor(props) {
        super(props);
        this.state = {file: ''};
        // const [file, setFile] = useState()

        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleChange(event) {
        // setFile(event.target.files[0])
        const fileElement = event.target;
        const ONE_MEGABYTE = 1024;
        // check file selected
        if (fileElement.files.length > 0) {
            const fileSize = fileElement.files.item(0).size;
            const sizeInMb = Math.round((fileSize / ONE_MEGABYTE));
            console.log(sizeInMb)
            // The size of the file should be <= 1MB
            if (sizeInMb > ONE_MEGABYTE) {
                alert("File too Big, please select a file less than 1MB");
            }
        }
        this.setState({file: event.target.files[0]});
    }


    handleSubmit(event) {
        event.preventDefault()
        const url = 'https://netcracker.onrender.com/upload';
        const formData = new FormData();
        formData.append('file', this.state.file);
        formData.append('fileName', this.state.file.name);
        const config = {
            headers: {
                // 'content-type': 'multipart/form-data',
                'Content-Type': 'application/x-www-form-urlencoded'
            },
        };
        axios.post(url, formData, config).then((response) => {
            console.log(response.data);
            window.location.reload();
        });
    }

    render() {
        return (
            <div className="App">
                <form onSubmit={this.handleSubmit}>
                    {/*<input type="file" accept=".pdf" onChange={this.handleChange}/>*/}
                    {/*<button type="submit">Upload</button>*/}
                    <table>
                        <tr>
                            <td>
                                Upload File:
                            </td>
                            <td>
                                <input id="file" type="file" accept=".pdf" onChange={this.handleChange}/>
                            </td>
                        </tr>
                        <tr>
                            <td></td>
                            <td>
                                <input type="submit" value="Upload" id="upload-button"/>
                            </td>
                        </tr>
                    </table>
                </form>
            </div>
        )
    }
}