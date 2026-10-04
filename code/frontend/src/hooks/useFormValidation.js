import { useCallback, useState } from "react";

/**
 * Hook validate ฟอร์มแบบง่าย ๆ ใช้ร่วมกับฟอร์มที่ต้องเช็คก่อนยิง API
 *
 * ตัวอย่างการใช้งาน:
 *   const { errors, validate, reset } = useFormValidation({
 *       evidenceText: (value) =>
 *           !value || value.trim().length < 5
 *               ? "กรุณากรอกอย่างน้อย 5 ตัวอักษร"
 *               : null,
 *   });
 *
 *   function handleSubmit(e) {
 *       e.preventDefault();
 *       if (!validate({ evidenceText })) return;
 *       // ผ่านการตรวจสอบแล้ว ค่อยยิง API ต่อ
 *   }
 *
 * rules: { [field: string]: (value, allValues) => string | null }
 */
export function useFormValidation(rules = {}) {
    const [errors, setErrors] = useState({});

    const validate = useCallback(
        (values = {}) => {
            const nextErrors = {};
            for (const field of Object.keys(rules)) {
                const message = rules[field](values[field], values);
                if (message) {
                    nextErrors[field] = message;
                }
            }
            setErrors(nextErrors);
            return Object.keys(nextErrors).length === 0;
        },
        [rules],
    );

    const clearError = useCallback((field) => {
        setErrors((prev) => {
            if (!(field in prev)) return prev;
            const next = { ...prev };
            delete next[field];
            return next;
        });
    }, []);

    const reset = useCallback(() => setErrors({}), []);

    return { errors, validate, clearError, reset };
}

export default useFormValidation;
