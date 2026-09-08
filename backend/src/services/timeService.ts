class TimeService {

    private formatNumberWithSign(num:number): string {

        num = Math.abs(num);
        let formattedNumber = num.toString();

        if (this.isSingleDigit(num)){
            formattedNumber = this.formatSingleDigit(num);
        }

        if (num > 0) {
            return `+${formattedNumber}`;
        } else if (num < 0) {
            return `-${formattedNumber}`;
        }

        return formattedNumber;
    }

    private formatSingleDigit(digit: number):string {
        return `0${digit}`;
    }

    private isSingleDigit(digit: number):boolean {
        return Math.abs(digit) < 10;
    }

    getTime() {
        const timeOptions: Intl.DateTimeFormatOptions = {
            hour12: false,
        };

        const date = new Date()

        const time = date.toLocaleTimeString(undefined, timeOptions);

        const utcDiff = date.getTimezoneOffset() * -1;

        const offsetHours = utcDiff / 60;
        const offsetMinutes = utcDiff % 60;


        return `${time} GMT${this.formatNumberWithSign(offsetHours)}:${this.formatNumberWithSign(offsetMinutes)}`;
    }

}

export default TimeService;