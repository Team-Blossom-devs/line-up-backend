const sendData = async (waitingId, tableNumber, tableCount) => {
    if (tableNumber === null || tableCount === null){
        console.log(tableNumber);
        return;
    }

    await axios.patch("https://www.blossom-server.com/api/manager/complete", {
        id: waitingId,
        tableNumber: tableNumber,
        tableCount: tableCount
    })
        .then((response) => {
            console.log(response)
            if (response.data.code === 'COM-000'){
                window.alert('입장 처리 완료');
                window.location.href = response.data.data.redirectUrl;
            } else {
                throw new Error();
            }
        })
        .catch((error) => {
            console.error('Error: ', error);
            window.alert('입장 처리에 실패하였습니다. 운영자에게 문의하세요.');
        })
}

function getInputValue(idName) {
    const inputValue = document.getElementById(idName).value;
    if (inputValue === '') {
        window.alert('입력 값이 필요합니다!')
        return null;
    }
    return parseInt(inputValue);
}