import Contacts

/// 폰 연락처를 읽어오는 서비스 (Contacts 프레임워크).
enum ContactsService {

    /// 연락처 접근 권한을 요청한다.
    static func requestAccess() async -> Bool {
        let store = CNContactStore()
        do {
            return try await store.requestAccess(for: .contacts)
        } catch {
            return false
        }
    }

    /// 이름·전화번호가 있는 연락처를 이름순으로 불러온다.
    static func loadContacts() -> [Contact] {
        let store = CNContactStore()
        let keys: [CNKeyDescriptor] = [
            CNContactGivenNameKey as CNKeyDescriptor,
            CNContactFamilyNameKey as CNKeyDescriptor,
            CNContactPhoneNumbersKey as CNKeyDescriptor,
        ]
        let request = CNContactFetchRequest(keysToFetch: keys)
        request.sortOrder = .userDefault

        var result: [Contact] = []
        do {
            try store.enumerateContacts(with: request) { cn, _ in
                guard let number = cn.phoneNumbers.first?.value.stringValue else { return }
                let name = CNContactFormatter.string(from: cn, style: .fullName)
                    ?? "\(cn.familyName)\(cn.givenName)"
                let trimmed = name.trimmingCharacters(in: .whitespaces)
                guard !trimmed.isEmpty else { return }
                result.append(Contact(id: cn.identifier, name: trimmed, phone: number))
            }
        } catch {
            return []
        }
        return result.sorted { $0.name.localizedCompare($1.name) == .orderedAscending }
    }
}
