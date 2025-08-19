# Little Hiker Friends API 가이드 (iOS 개발자용)

## 🚀 시작하기

### 1. API 문서 확인
서버 실행 후 다음 URL에서 API 문서를 확인할 수 있습니다:
```
http://localhost:8080/swagger-ui.html
```

### 2. Base URL
```
http://localhost:8080/api
```

## 📍 위치 관리 API

### 위치 업데이트
사용자의 현재 위치를 서버에 전송합니다.

**Swift 코드 예시:**
```swift
struct LocationRequest: Codable {
    let latitude: Double
    let longitude: Double
    let altitude: Double?
}

struct LocationResponse: Codable {
    let id: Int
    let userId: Int
    let userNickname: String
    let latitude: Double
    let longitude: Double
    let altitude: Double?
    let timestamp: String
}

func updateLocation(latitude: Double, longitude: Double, altitude: Double?) {
    let url = URL(string: "http://localhost:8080/api/locations")!
    var request = URLRequest(url: url)
    request.httpMethod = "POST"
    request.setValue("application/json", forHTTPHeaderField: "Content-Type")
    
    let locationData = LocationRequest(
        latitude: latitude,
        longitude: longitude,
        altitude: altitude
    )
    
    do {
        request.httpBody = try JSONEncoder().encode(locationData)
        
        URLSession.shared.dataTask(with: request) { data, response, error in
            if let data = data {
                let locationResponse = try? JSONDecoder().decode(LocationResponse.self, from: data)
                // 응답 처리
                print("위치 업데이트 성공: \(locationResponse?.id ?? 0)")
            }
        }.resume()
    } catch {
        print("인코딩 에러: \(error)")
    }
}
```

### 등산 모임 멤버 위치 조회
특정 등산 모임에 참여한 멤버들의 위치를 가져옵니다.

**Swift 코드 예시:**
```swift
func getHikeMembersLocations(hikeId: Int, completion: @escaping ([LocationResponse]) -> Void) {
    let url = URL(string: "http://localhost:8080/api/locations/hikes/\(hikeId)/members")!
    
    URLSession.shared.dataTask(with: url) { data, response, error in
        if let data = data {
            do {
                let locations = try JSONDecoder().decode([LocationResponse].self, from: data)
                DispatchQueue.main.async {
                    completion(locations)
                }
            } catch {
                print("디코딩 에러: \(error)")
            }
        }
    }.resume()
}

// 사용 예시
getHikeMembersLocations(hikeId: 1) { locations in
    // 지도에 마커 추가
    for location in locations {
        let coordinate = CLLocationCoordinate2D(
            latitude: location.latitude,
            longitude: location.longitude
        )
        // 지도에 마커 추가 로직
    }
}
```

## 🗺️ MapKit과 연동

### 위치를 지도에 표시하기
```swift
import MapKit

class HikeMapViewController: UIViewController {
    @IBOutlet weak var mapView: MKMapView!
    
    func displayMemberLocations(locations: [LocationResponse]) {
        // 기존 마커 제거
        mapView.removeAnnotations(mapView.annotations)
        
        // 새 마커 추가
        for location in locations {
            let annotation = MKPointAnnotation()
            annotation.coordinate = CLLocationCoordinate2D(
                latitude: location.latitude,
                longitude: location.longitude
            )
            annotation.title = location.userNickname
            annotation.subtitle = "고도: \(location.altitude ?? 0)m"
            
            mapView.addAnnotation(annotation)
        }
        
        // 지도 영역 조정
        if !locations.isEmpty {
            let coordinates = locations.map { 
                CLLocationCoordinate2D(latitude: $0.latitude, longitude: $0.longitude) 
            }
            let region = MKCoordinateRegion(coordinates: coordinates)
            mapView.setRegion(region, animated: true)
        }
    }
}

extension MKCoordinateRegion {
    init(coordinates: [CLLocationCoordinate2D]) {
        let latitudes = coordinates.map { $0.latitude }
        let longitudes = coordinates.map { $0.longitude }
        
        let minLat = latitudes.min() ?? 0
        let maxLat = latitudes.max() ?? 0
        let minLon = longitudes.min() ?? 0
        let maxLon = longitudes.max() ?? 0
        
        let center = CLLocationCoordinate2D(
            latitude: (minLat + maxLat) / 2,
            longitude: (minLon + maxLon) / 2
        )
        
        let span = MKCoordinateSpan(
            latitudeDelta: (maxLat - minLat) * 1.2,
            longitudeDelta: (maxLon - minLon) * 1.2
        )
        
        self.init(center: center, span: span)
    }
}
```

## 🔄 실시간 위치 업데이트

### Core Location으로 위치 획득 후 서버 전송
```swift
import CoreLocation

class LocationManager: NSObject, CLLocationManagerDelegate {
    private let locationManager = CLLocationManager()
    
    override init() {
        super.init()
        locationManager.delegate = self
        locationManager.requestWhenInUseAuthorization()
        locationManager.desiredAccuracy = kCLLocationAccuracyBest
    }
    
    func startLocationUpdates() {
        locationManager.startUpdatingLocation()
    }
    
    func stopLocationUpdates() {
        locationManager.stopUpdatingLocation()
    }
    
    // MARK: - CLLocationManagerDelegate
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        guard let location = locations.last else { return }
        
        // 서버에 위치 전송
        updateLocation(
            latitude: location.coordinate.latitude,
            longitude: location.coordinate.longitude,
            altitude: location.altitude
        )
    }
}
```

## 📱 앱에서 사용하는 전체 플로우

1. **앱 시작 시**: 사용자 인증 후 등산 모임 목록 조회
2. **등산 모임 선택**: 특정 모임에 참여
3. **위치 공유 시작**: GPS로 현재 위치 획득 → 서버 전송
4. **다른 멤버 위치 조회**: 주기적으로 API 호출하여 최신 위치 가져오기
5. **지도 업데이트**: 받은 위치 데이터를 지도에 표시

## 🛠️ 개발 팁

- **에러 처리**: 네트워크 에러, GPS 권한 등을 적절히 처리
- **배터리 최적화**: 위치 업데이트 주기를 적절히 조절
- **오프라인 대응**: 네트워크 연결이 끊어졌을 때의 처리
- **캐싱**: 최근 위치 데이터를 로컬에 저장하여 UX 개선
