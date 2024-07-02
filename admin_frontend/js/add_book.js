let prefixUrl = 'http://localhost:8080';

drawCategories()

function drawCategories() {
    axios({
        url: prefixUrl + '/api/admin/category',
        method: 'GET',
        headers: {
            'Authorization': localStorage.getItem('accessToken')
        }
    }).then(resp => {
        let innerHtml = ''
        resp.data.map(category => {
            innerHtml += `<option value="${category.id}">${category.name}</option>`
        })
        document.getElementById('genres').innerHTML = innerHtml;
    }).catch(e => {
        axios({
            url: prefixUrl + '/api/refresh',
            method: 'GET',
            headers: {
                'Authorization': localStorage.getItem('refreshToken')
            }
        }).then(resp => {
            console.log(resp.data);
            localStorage.setItem('accessToken', resp.data)
            drawCategories()
        }).catch(e => {
            window.location.href = 'login.html'
        })
    })
}

const form = document.getElementById('bookForm');

form.addEventListener('submit', (event) => sendData(event));

function sendData(event) {
    event.preventDefault();
    fetch(form.action, {
        method: 'POST',
        body: new FormData(form),
        headers: {
            'Authorization': localStorage.getItem('accessToken')
        }
    })
        .then(response => {
            window.location.href = 'admin.html';
        })
        .catch(e => {
            axios({
                url: prefixUrl + '/api/refresh',
                method: 'GET',
                headers: {
                    'Authorization': localStorage.getItem('refreshToken')
                }
            }).then(resp => {
                console.log(resp.data);
                localStorage.setItem('accessToken', resp.data.accessToken)
                sendData(event)
            }).catch(e => {
                window.location.href = 'login.html'
            })
        })
}