class Role {
    id;
    name;
}

class User {
    id;
    firstName;
    lastName;
    age;
    email;
    password;
    roles = [];
}

const url_users = '/adminAPI/list';
const url_roles = '/adminAPI/roles';
const url_save_user = '/adminAPI/saveUser';
const url_delete_user = '/adminAPI/deleteUser';
let url_user = '/userAPI/getCurrentUser';

let adminAPI = function () {

    return {
        getUsers: async function () {
            let users_json;
            let response = await fetch(url_users);
            if (response.ok) {
                users_json = await response.json();
            }
            return users_json;
        },

        getRoles: async function () {
            let roles_json;
            let response = await fetch(url_roles);
            if (response.ok) {
                roles_json = await response.json();
            }
            return roles_json;
        },

        getUser: async function () {
            let user_json;
            let response = await fetch(url_user);
            if (response.ok) {
                user_json = await response.json();
            } else {
                alert("Ошибка HTTP: " + response.status);
            }
            return user_json;
        },


        saveUser: async function (user) {
            let response = await fetch(url_save_user, {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json;charset=utf-8'
                },
                body: JSON.stringify(user)
            });
            if (response.ok) {
                await response;
            } else {
                alert("Ошибка HTTP: " + response.status);
            }

        },

        deleteUser: async function (user) {
            let response = await fetch(url_delete_user, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json;charset=utf-8'
                },
                body: JSON.stringify(user)
            });
            if (response.ok) {
                await response;
            } else {
                alert("Ошибка HTTP: " + response.status);
            }

        }
    }
};


$(function () {
    let api = adminAPI();

    function updateUsers() {
        let tbody = $('#body_users_table');
        let tbody_user = $('#body_user_table');
        let head_text = $('#header_text');
        let roles_text;
        tbody.empty();
        tbody_user.empty();

        //заполняем вкладку User
        api.getUser().then(user_json => {
            roles_text = user_json.roles.map(r => r.name).map(r => r.replaceAll("ROLE_", "")).join(' ');
            let tr = $('<tr/>')
                .append($('<td/>').text(user_json.id))
                .append($('<td/>').text(user_json.firstName))
                .append($('<td/>').text(user_json.lastName))
                .append($('<td/>').text(user_json.age))
                .append($('<td/>').text(user_json.email))
                .append($('<td/>').append($('<span/>').text(roles_text)));
            tbody_user.append(tr);
            head_text.text(user_json.email + " with roles: " + roles_text);
        });

        //заполняем вкладку Admin
        api.getUsers().then(users_json => {
            for (let i = 0; i < users_json.length; i++) {
                let user = new User();//пользователь для передачи в модальное окно
                user.id = users_json[i].id;
                user.firstName = users_json[i].firstName;
                user.lastName = users_json[i].lastName;
                user.age = users_json[i].age;
                user.email = users_json[i].email;
                user.roles = users_json[i].roles;

                roles_text = users_json[i].roles.map(r => r.name).map(r => r.replaceAll("ROLE_", "")).join(' ');
                let tr = $('<tr/>')
                    .append($('<td/>').text(users_json[i].id))
                    .append($('<td/>').text(users_json[i].firstName))
                    .append($('<td/>').text(users_json[i].lastName))
                    .append($('<td/>').text(users_json[i].age))
                    .append($('<td/>').text(users_json[i].email))
                    .append($('<td/>').append($('<span/>').text(roles_text)))
                    .append($('<td/>')
                        .append('<button type="button" class="btn btn-info btn-sm ml-4 mr-2" data-user = ' + JSON.stringify(user) + ' data-toggle="modal" data-target="#updateModal">Edit</button>'))
                    .append($('<td/>')
                        .append('<button type="button" class="btn btn-danger btn-sm"  data-user = ' + JSON.stringify(user) + '  data-toggle="modal" data-target="#deleteModal">Delete</button>'));
                tbody.append(tr);
            }
        });

        //добавляем все возможные роли в селект на закладке нового пользователя, в модальных окнах удаления и редактирования
        api.getRoles().then(roles_json => {

            $('#user_roles_new_user').find('option').remove();
            $('#user_roles_update_modal').find('option').remove();
            $('#user_roles_delete_modal').find('option').remove();

            let roles = $('#user_roles_new_user');
            $.each(roles_json, function (key, value) {
                roles.append('<option value="' + value.id + '">' + value.name.replaceAll("ROLE_", "") + '</option>');
            });
            roles = $('#user_roles_delete_modal');
            $.each(roles_json, function (key, value) {
                roles.append('<option value="' + value.id + '">' + value.name.replaceAll("ROLE_", "") + '</option>');
            });
            roles = $('#user_roles_update_modal');
            $.each(roles_json, function (key, value) {
                roles.append('<option value="' + value.id + '">' + value.name.replaceAll("ROLE_", "") + '</option>');
            });
        })

    }

    //собираем нового пользователя и отправляем запрос на его создание кликом по кнопке с id= "button_new_user"
    $('#button_new_user').click(function () {
        let new_user = new User();

        // заполняем нового пользователя данными (у всех полей ввода на вкладке с id="newuser" атрибут class="form-control"
        $('#newuser .form-control').each(function (index, element) {
            new_user[element.name] = element.value;
        });
        new_user.id = 0;//поскольку новый , то id=0

        //получаем массив выбранных ролей и добавляем их новому пользователю
        let userRolesSelect = $('#user_roles_new_user');
        let selected_roles = userRolesSelect.find('option:selected').map(function () {
            let role = new Role();
            role.id = $(this).val();
            role.name = "ROLE_" + $(this).text();
            return role;
        }).toArray();
        new_user.roles = selected_roles;

        //очищаем поля input  и селектора ролей
        //отправляем нового пользователя и обновляем таблицу пользователей
        api.saveUser(new_user).then(r => {
            $('#newuser').find('input').val('');
            $('#user_roles_new_user').find('option').remove();
            updateUsers();
            $('.nav-tabs a[href="#users"]').tab('show');
        });

    });


    //заполняем данные после отрисовки модального окна редактирования пользователя
    $('#updateModal').on('shown.bs.modal', function (e) {
        let user = JSON.parse(e.relatedTarget.dataset.user);
        user.password = '';
        let roles = user.roles;
        $('#updateModal .form-control').each(function (index, element) {
            element.value = user[element.name];
        });
        //заполняем текущие роли пользователя
        for (let i = 0; i < roles.length; i++) {
            $('#user_roles_update_modal option[value = ' + roles[i].id + ']').prop('selected', true);
        }

    })


//заполняем данные после отрисовки модального окна удаления пользователя
    $('#deleteModal').on('shown.bs.modal', function (e) {
        let user = JSON.parse(e.relatedTarget.dataset.user);
        user.password = '';
        let roles = user.roles;
        $('#deleteModal .form-control').each(function (index, element) {
            element.value = user[element.name];
        });
        //заполняем текущие роли пользователя
        for (let i = 0; i < roles.length; i++) {
            $('#user_roles_delete_modal option[value = ' + roles[i].id + ']').prop('selected', true);
        }
    })

    $('#button_edit_user').click(function () {
        let new_user = new User();

        // заполняем нового пользователя данными (у всех полей ввода на вкладке с id="newuser" атрибут class="form-control"
        $('#updateModal .form-control').each(function (index, element) {
            new_user[element.name] = element.value;
        });
        console.log(new_user);
        //получаем массив выбранных ролей и добавляем их новому пользователю
        let userRolesSelect = $('#user_roles_update_modal');
        let selected_roles = userRolesSelect.find('option:selected').map(function () {
            let role = new Role();
            role.id = $(this).val();
            role.name = "ROLE_" + $(this).text();
            return role;
        }).toArray();
        new_user.roles = selected_roles;

        //очищаем поля input  и селектора ролей
        //отправляем нового пользователя и обновляем таблицу пользователей
        api.saveUser(new_user).then(r => {
            $('#updateModal').find('input').val('');
            $('#user_roles_update_modal').find('option').remove();
            $("#updateModal").modal('hide');
            updateUsers();
            $('.nav-tabs a[href="#users"]').tab('show');
        });
    });

    $('#button_delete_user').click(function () {
        let new_user = new User();

        // заполняем нового пользователя данными (у всех полей ввода на вкладке с id="newuser" атрибут class="form-control"
        $('#deleteModal .form-control').each(function (index, element) {
            new_user[element.name] = element.value;
        });
        console.log(new_user);
        //получаем массив выбранных ролей и добавляем их новому пользователю
        let userRolesSelect = $('#user_roles_delete_modal');
        let selected_roles = userRolesSelect.find('option:selected').map(function () {
            let role = new Role();
            role.id = $(this).val();
            role.name = "ROLE_" + $(this).text();
            return role;
        }).toArray();
        new_user.roles = selected_roles;

        //очищаем поля input  и селектора ролей
        //отправляем нового пользователя и обновляем таблицу пользователей
        api.deleteUser(new_user).then(r => {
            $('#deleteModal').find('input').val('');
            $('#user_roles_delete_modal').find('option').remove();
            $("#deleteModal").modal('hide');
            updateUsers();
            $('.nav-tabs a[href="#users"]').tab('show');
        });
    });

    updateUsers();
});
