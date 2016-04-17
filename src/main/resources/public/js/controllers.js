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
        const categoryIds = $scope.view.categories
            .filter((cat) => cat.selected)
            .map((cat) => cat.id);
        const data = {
            "key": $scope.view.apiKey,
            "zip": $scope.view.zip,
            "radius": $scope.view.radius,
            "categoryIds": categoryIds
        };
        $http.post("/groups", data)
            .then((data) =>
                $scope.view.groups = data.data
            );
    };

    $scope.groupSubmit = function () {
        const groupIds = $scope.view.groups
            .filter((grp) => grp.selected)
            .map((grp) => grp.id);
        const data = {
            "key": $scope.view.apiKey,
            "groupIds": groupIds
        };
        $http.post("/members", data)
            .then((data) =>
                $scope.view.members = data.data
            );
    };
}]);