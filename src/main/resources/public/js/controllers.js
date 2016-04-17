"use strict";

app.controller('MainController', ['$scope', '$http', function ($scope, $http) {

    $scope.view = {
        message: 'Hello world!!!'
    };

    $scope.apiKeySubmit = function () {
        const url = "/categories?key=" + $scope.view.apiKey;
        $http.get(url)
            .then((data) => $scope.view.categories = data.data.results);
    };
}]);