getBooks()

function getBooks() {
    axios({
        url: 'http://localhost:8080/api/admin/book',
        method: 'GET',
        headers: {
            'Authorization': localStorage.getItem('token')
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
            url: 'http://localhost:8080/api/refresh',
            method: 'GET',
            headers: {
                'Authorization': localStorage.getItem('refreshToken')
            }
        }).then(resp => {
            console.log(resp.data);
            localStorage.setItem('token', resp.data)
            getBooks()
        }).catch(e => {
            window.location.href = 'login.html'
        })
    })
}

function deleteBook(bookId) {
    axios({
        url: `http://localhost:8080/api/admin/book/${bookId}`,
        method: 'DELETE',
        headers: {
            'Authorization': localStorage.getItem('token')
        }
    }).then(resp => {
        getBooks();
    }).catch(e => {
        console.log(e);
        axios({
            url: 'http://localhost:8080/api/refresh',
            method: 'GET',
            headers: {
                'Authorization': localStorage.getItem('refreshToken')
            }
        }).then(resp => {
            console.log(resp.data);
            localStorage.setItem('token', resp.data)
            // deleteBook(bookId)
        }).catch(e => {
            console.log(e);
            window.location.href = 'login.html'
        })
    })
}