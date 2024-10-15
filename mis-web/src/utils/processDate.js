export function processDate(data, closeTimeRef, startTimeRef) {
  if (data.infoArray && data.infoArray.length > 0) {
    const exeYear = data.infoArray[0].d.substring(0, 4);
    const exeMonth = data.infoArray[0].d.substring(4, 6) - 1;
    const exeDay = data.infoArray[0].d.substring(6);

    const dtB = new Date(exeYear, exeMonth, exeDay, 13, 33, 0);
    closeTimeRef.value = dtB.getTime();

    const dtA = new Date(exeYear, exeMonth, exeDay, 9, 0, 0);
    startTimeRef.value = dtA.getTime();
  }
}
