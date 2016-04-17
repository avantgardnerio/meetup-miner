"use strict";

app.controller('MainController', ['$scope', '$http', function ($scope, $http) {

    $scope.view = {
        radius: 50
    };

    $scope.apiKeySubmit = function () {
        const url = "/categories?key=" + $scope.view.apiKey;
        $http.get(url)
            .then((data) =>
                $scope.view.categories = data.data.results
            );
    };

    $scope.zipCodeSubmit = function () {
        const categoryId = $scope.view.categories
            .filter((cat) => cat.selected)
            .map((cat) => cat.id)[0];
        const url = "/groups"
                + "?key=" + $scope.view.apiKey
                + "&zip=" + $scope.view.zip
                + "&radius=" + $scope.view.radius
                + "&category_id=" + categoryId
            ;
        $http.get(url)
            .then((data) =>
                $scope.view.groups = data.data.results
            );
    };
}]);