function compareStoreBroughtFromAndIds(a, b) {
    // Use toUpperCase() to ignore character casing
    const itemA = a.storeOrderedFrom;
    const itemB = b.storeOrderedFrom;

    let comparison = 0;
    if (itemA > itemB) {
        comparison = 1;
    } else if (itemA < itemB) {
        comparison = -1;
    } else{
        const idA = a.id;
        const idB = b.id;
        if (idA> idB){
            comparison = 1;
        } else if (idA < idB){
            comparison = -1;
        }
    }
    return comparison;
}

function compareItemID(a, b) {
    // Use toUpperCase() to ignore character casing
    const itemA = a.id;
    const itemB = b.id;

    let comparison = 0;
    if (itemA > itemB) {
        comparison = 1;
    } else if (itemA < itemB) {
        comparison = -1;
    }
    return comparison;
}

function compareDiscountName(a, b) {
    // Use toUpperCase() to ignore character casing
    const itemA = a.discountName;
    const itemB = b.discountName;

    let comparison = 0;
    if (itemA > itemB) {
        comparison = 1;
    } else if (itemA < itemB) {
        comparison = -1;
    }
    return comparison;
}


function compareStoreID(a, b) {
    // Use toUpperCase() to ignore character casing
    const storeA = a.storeID;
    const storeB = b.storeID;

    let comparison = 0;
    if (storeA > storeB) {
        comparison = 1;
    } else if (storeA < storeB) {
        comparison = -1;
    }
    return comparison;
}