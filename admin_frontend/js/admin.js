let prefixUrl = 'http://localhost:8080';

getBooks()

function getBooks() {
    axios({
        url: prefixUrl + '/api/admin/book',
        method: 'GET',
        headers: {
            'Authorization': localStorage.getItem('accessToken')
        }
    }).then(resp => {
        let innerHtml = '';
        resp.data.map(book => {
            let categories = '';
            for (const category of book.categories) {
                categories += category + ', ';
            }
            innerHtml += `
            <tr>
            <td>${book.title}</td>
            <td>${book.author}</td>
            <td>${book.description}</td>
            <td>${categories}</td>
            <td>${new Date(book.createdAt).toDateString()}</td>
            <td><button class="btn btn-warning text-white" onclick="deleteBook('${book.id}')">Delete</button></td>
            </tr>`
        })
        document.getElementById('tbody').innerHTML = innerHtml;
    }).catch(e => {
        axios({
            url:  prefixUrl + '/api/refresh',
            method: 'GET',
            headers: {
                'Authorization': localStorage.getItem('refreshToken')
            }
        }).then(resp => {
            console.log(resp.data);
            localStorage.setItem('accessToken', resp.data.accessToken)
            getBooks()
        }).catch(e => {
            window.location.href = 'login.html'
        })
    })
}

function deleteBook(bookId) {
    axios({
        url:  prefixUrl + `/api/admin/book/${bookId}`,
        method: 'DELETE',
        headers: {
            'Authorization': localStorage.getItem('accessToken')
        }
    }).then(resp => {
        getBooks();
    }).catch(e => {
        console.log(e);
        axios({
            url: prefixUrl + '/api/refresh',
            method: 'GET',
            headers: {
                'Authorization': localStorage.getItem('refreshToken')
            }
        }).then(resp => {
            console.log(resp.data);
            localStorage.setItem('accessToken', resp.data.accessToken)
        }).catch(e => {
            console.log(e);
            window.location.href = 'login.html'
        })
    })
}